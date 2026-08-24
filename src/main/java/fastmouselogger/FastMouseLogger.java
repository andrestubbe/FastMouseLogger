package fastmouselogger;

import fastmouse.FastMouse;
import fastmouse.FastMouseListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * High-performance background mouse recorder and continuous binary streamer.
 */
public class FastMouseLogger implements FastMouseListener, AutoCloseable {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

    private final FastMouse mouse;
    private final Path outputDirectory;
    private final int bufferFlushThreshold;
    private final List<MouseEventRecord> memoryBuffer = Collections.synchronizedList(new ArrayList<>(1024));
    private final List<MouseEventListener> eventListeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean recording = new AtomicBoolean(false);

    public FastMouseLogger(Path outputDirectory) {
        this(outputDirectory, 5000);
    }

    public FastMouseLogger(Path outputDirectory, int bufferFlushThreshold) {
        this.outputDirectory = outputDirectory;
        this.bufferFlushThreshold = bufferFlushThreshold;
        this.mouse = FastMouse.open();
        try {
            if (outputDirectory != null) {
                Files.createDirectories(outputDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize mouse log directory", e);
        }
    }

    /**
     * Starts listening to raw mouse input.
     */
    public synchronized void start() {
        if (recording.compareAndSet(false, true)) {
            mouse.startListening(this);
        }
    }

    /**
     * Stops listening and flushes buffer to disk.
     */
    public synchronized void stop() {
        if (recording.compareAndSet(true, false)) {
            mouse.stopListening();
            flush();
        }
    }

    public boolean isRecording() {
        return recording.get();
    }

    public void addListener(MouseEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeListener(MouseEventListener listener) {
        eventListeners.remove(listener);
    }

    @Override
    public void onMouseMove(long deviceHandle, int deltaX, int deltaY, int absoluteX, int absoluteY) {
        if (!recording.get()) return;
        recordEvent(new MouseEventRecord(System.currentTimeMillis(), MouseEventRecord.FLAG_MOVE, deltaX, deltaY, 0));
    }

    @Override
    public void onMouseButton(long deviceHandle, int buttonId, boolean isPressed) {
        if (!recording.get()) return;
        int flag = MouseEventRecord.FLAG_MOVE;
        if (buttonId == 0) {
            flag = isPressed ? MouseEventRecord.FLAG_LEFT_DOWN : MouseEventRecord.FLAG_LEFT_UP;
        } else if (buttonId == 1) {
            flag = isPressed ? MouseEventRecord.FLAG_RIGHT_DOWN : MouseEventRecord.FLAG_RIGHT_UP;
        } else if (buttonId == 2) {
            flag = isPressed ? MouseEventRecord.FLAG_MIDDLE_DOWN : MouseEventRecord.FLAG_MIDDLE_UP;
        }
        recordEvent(new MouseEventRecord(System.currentTimeMillis(), flag, 0, 0, 0));
    }

    @Override
    public void onMouseWheel(long deviceHandle, int delta) {
        if (!recording.get()) return;
        recordEvent(new MouseEventRecord(System.currentTimeMillis(), MouseEventRecord.FLAG_WHEEL, 0, 0, delta));
    }

    private void recordEvent(MouseEventRecord record) {
        memoryBuffer.add(record);

        for (MouseEventListener listener : eventListeners) {
            listener.onMouseEvent(record);
        }

        if (outputDirectory != null && memoryBuffer.size() >= bufferFlushThreshold) {
            flush();
        }
    }

    /**
     * Flushes currently buffered mouse records to a timestamped .mousebin file.
     */
    public synchronized void flush() {
        if (memoryBuffer.isEmpty() || outputDirectory == null) {
            return;
        }

        List<MouseEventRecord> snapshot;
        synchronized (memoryBuffer) {
            snapshot = new ArrayList<>(memoryBuffer);
            memoryBuffer.clear();
        }

        String fileName = LocalDateTime.now().format(FILE_DATE_FORMAT) + ".mousebin";
        Path targetFile = outputDirectory.resolve(fileName);
        try {
            MousebinCodec.writeToFile(targetFile, snapshot);
        } catch (IOException e) {
            System.err.println("Failed to write mouse records to " + targetFile + ": " + e.getMessage());
        }
    }

    /**
     * Returns a snapshot of memory-buffered records.
     */
    public List<MouseEventRecord> getBufferedRecords() {
        synchronized (memoryBuffer) {
            return new ArrayList<>(memoryBuffer);
        }
    }

    @Override
    public void close() {
        stop();
    }
}
