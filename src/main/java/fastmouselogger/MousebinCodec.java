package fastmouselogger;

import fastfileformat.BinaryHeader;
import fastfileformat.BinaryReader;
import fastfileformat.BinaryWriter;
import fastfileformat.FastFileFormat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * High-speed binary serializer and stream decoder for mouse event log files (.mousebin).
 * Built on top of FastFileFormat and FastBinary VarInt compression.
 */
public final class MousebinCodec {
    /**
     * Payload type identifier for FastJava Raw Mouse Logs (0x0003).
     */
    public static final short PAYLOAD_TYPE_MOUSEBIN = 0x0003;

    private MousebinCodec() {}

    /**
     * Encodes a list of mouse events into a compressed FastFileFormat binary byte array.
     *
     * @param events List of mouse events.
     * @return Compact binary byte array with 12-byte header.
     */
    public static byte[] encode(List<MouseEventRecord> events) {
        if (events == null || events.isEmpty()) {
            BinaryWriter finalWriter = FastFileFormat.binaryWriter(12);
            finalWriter.writeHeader(FastFileFormat.DEFAULT_MAGIC, FastFileFormat.DEFAULT_VERSION, PAYLOAD_TYPE_MOUSEBIN, 0);
            return finalWriter.toByteArray();
        }

        BinaryWriter payloadWriter = FastFileFormat.binaryWriter(events.size() * 12);
        payloadWriter.writeVarInt(events.size());

        long baseTime = events.get(0).timestamp();
        payloadWriter.writeLong(baseTime);

        long lastTime = baseTime;
        for (MouseEventRecord ev : events) {
            long delta = ev.timestamp() - lastTime;
            lastTime = ev.timestamp();

            payloadWriter.writeVarLong(delta);
            payloadWriter.writeByte((byte) ev.flags());
            payloadWriter.writeVarInt(ev.dx());
            payloadWriter.writeVarInt(ev.dy());
            if ((ev.flags() & MouseEventRecord.FLAG_WHEEL) != 0) {
                payloadWriter.writeVarInt(ev.wheelDelta());
            }
        }

        byte[] payload = payloadWriter.toByteArray();

        BinaryWriter finalWriter = FastFileFormat.binaryWriter(12 + payload.length);
        finalWriter.writeHeader(
                FastFileFormat.DEFAULT_MAGIC,
                FastFileFormat.DEFAULT_VERSION,
                PAYLOAD_TYPE_MOUSEBIN,
                payload.length
        );
        finalWriter.writeBytes(payload);
        return finalWriter.toByteArray();
    }

    /**
     * Decodes a .mousebin binary payload into a list of MouseEventRecord instances.
     *
     * @param bytes Binary payload.
     * @return List of reconstructed MouseEventRecords.
     */
    public static List<MouseEventRecord> decode(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return Collections.emptyList();
        }

        BinaryReader reader = FastFileFormat.binaryReader(bytes);
        BinaryHeader header = reader.readHeader();

        if (header.getMagic() != FastFileFormat.DEFAULT_MAGIC) {
            throw new IllegalArgumentException("Invalid FastFileFormat magic header: " + Integer.toHexString(header.getMagic()));
        }
        if (header.getPayloadType() != PAYLOAD_TYPE_MOUSEBIN) {
            throw new IllegalArgumentException("Unexpected payload type for Mousebin: " + header.getPayloadType());
        }
        if (header.getPayloadLength() == 0) {
            return Collections.emptyList();
        }

        int count = reader.readVarInt();
        long currentTimestamp = reader.readLong();

        List<MouseEventRecord> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            long delta = reader.readVarLong();
            currentTimestamp += delta;

            int flags = reader.readByte() & 0xFF;
            int dx = reader.readVarInt();
            int dy = reader.readVarInt();
            int wheel = 0;
            if ((flags & MouseEventRecord.FLAG_WHEEL) != 0) {
                wheel = reader.readVarInt();
            }

            list.add(new MouseEventRecord(currentTimestamp, flags, dx, dy, wheel));
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Saves mouse events directly to a .mousebin file.
     */
    public static void writeToFile(Path path, List<MouseEventRecord> events) throws IOException {
        byte[] bytes = encode(events);
        Files.write(path, bytes);
    }

    /**
     * Reads mouse events directly from a .mousebin file.
     */
    public static List<MouseEventRecord> readFromFile(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return decode(bytes);
    }
}
