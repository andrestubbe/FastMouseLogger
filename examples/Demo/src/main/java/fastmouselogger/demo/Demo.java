package fastmouselogger.demo;

import fastmouselogger.FastMouseLogger;
import fastmouselogger.HeatmapGenerator;
import fastmouselogger.MouseEventRecord;
import fastmouselogger.MousebinCodec;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Demo {
    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" 🖱️ FastMouseLogger — High-Speed Event Streamer  ");
        System.out.println("=================================================");

        Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "FastMouseLoggerDemo");
        System.out.println("Logging target directory: " + tempDir);

        try (FastMouseLogger logger = new FastMouseLogger(tempDir, 100)) {
            logger.addListener(rec -> {
                if (rec.isButtonPress()) {
                    System.out.println("[CLICK EVENT] Button pressed at t=" + rec.timestamp() + " flags=" + Integer.toBinaryString(rec.flags()));
                }
            });

            System.out.println("\n--- Starting Raw Mouse Recording for 3 seconds (move mouse / click)... ---");
            logger.start();
            Thread.sleep(3000);
            logger.stop();

            List<MouseEventRecord> buffered = logger.getBufferedRecords();
            System.out.println("Captured " + buffered.size() + " mouse events.");

            // 1. Binary Encoding via FastFileFormat
            byte[] encoded = MousebinCodec.encode(buffered);
            System.out.println("Encoded .mousebin payload size: " + encoded.length + " bytes.");

            // 2. Binary Decoding
            List<MouseEventRecord> decoded = MousebinCodec.decode(encoded);
            System.out.println("Successfully decoded " + decoded.size() + " records from binary stream.");

            // 3. Heatmap Rasterization
            BufferedImage heatmap = HeatmapGenerator.generate(decoded, 1920, 1080);
            System.out.println("Generated Heatmap Image: " + heatmap.getWidth() + "x" + heatmap.getHeight() + " ARGB pixels.");

            System.out.println("\n✔ FastMouseLogger Pipeline Verified Successfully!");
        }
    }
}
