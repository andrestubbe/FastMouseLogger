package fastmouselogger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FastMouseLoggerTest {

    @Test
    public void testMousebinEncodingAndDecoding() {
        List<MouseEventRecord> events = new ArrayList<>();
        long baseTime = 1770000000000L;

        events.add(new MouseEventRecord(baseTime, MouseEventRecord.FLAG_MOVE, 10, -5, 0));
        events.add(new MouseEventRecord(baseTime + 15, MouseEventRecord.FLAG_LEFT_DOWN, 0, 0, 0));
        events.add(new MouseEventRecord(baseTime + 120, MouseEventRecord.FLAG_LEFT_UP, 0, 0, 0));
        events.add(new MouseEventRecord(baseTime + 250, MouseEventRecord.FLAG_WHEEL, 0, 0, 120));

        byte[] binary = MousebinCodec.encode(events);
        assertNotNull(binary);
        assertTrue(binary.length >= 12);

        List<MouseEventRecord> decoded = MousebinCodec.decode(binary);
        assertEquals(4, decoded.size());

        assertEquals(baseTime, decoded.get(0).timestamp());
        assertEquals(MouseEventRecord.FLAG_MOVE, decoded.get(0).flags());
        assertEquals(10, decoded.get(0).dx());
        assertEquals(-5, decoded.get(0).dy());

        assertEquals(baseTime + 15, decoded.get(1).timestamp());
        assertTrue(decoded.get(1).isLeftClick());

        assertEquals(baseTime + 250, decoded.get(3).timestamp());
        assertEquals(120, decoded.get(3).wheelDelta());
    }

    @Test
    public void testFilePersistence(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("test-session.mousebin");
        List<MouseEventRecord> events = List.of(
                new MouseEventRecord(1000L, MouseEventRecord.FLAG_MOVE, 5, 5, 0),
                new MouseEventRecord(1020L, MouseEventRecord.FLAG_RIGHT_DOWN, 0, 0, 0)
        );

        MousebinCodec.writeToFile(logFile, events);
        assertTrue(logFile.toFile().exists());
        assertTrue(logFile.toFile().length() > 0);

        List<MouseEventRecord> restored = MousebinCodec.readFromFile(logFile);
        assertEquals(2, restored.size());
        assertEquals(1000L, restored.get(0).timestamp());
        assertTrue(restored.get(1).isRightClick());
    }

    @Test
    public void testHeatmapGeneration() {
        List<MouseEventRecord> events = List.of(
                new MouseEventRecord(1000L, MouseEventRecord.FLAG_MOVE, 10, 10, 0),
                new MouseEventRecord(1050L, MouseEventRecord.FLAG_LEFT_DOWN, 0, 0, 0)
        );

        BufferedImage heatmap = HeatmapGenerator.generate(events, 200, 200);
        assertNotNull(heatmap);
        assertEquals(200, heatmap.getWidth());
        assertEquals(200, heatmap.getHeight());
    }
}
