package fastmouselogger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * High-performance heatmap accumulator and generator for mouse movement trajectories and click density.
 */
public final class HeatmapGenerator {

    private HeatmapGenerator() {}

    /**
     * Generates an ARGB heatmap image from mouse records.
     *
     * @param events List of mouse records.
     * @param width Screen width.
     * @param height Screen height.
     * @return Rendered heatmap BufferedImage.
     */
    public static BufferedImage generate(List<MouseEventRecord> events, int width, int height) {
        int[][] density = new int[width][height];
        int maxDensity = 1;

        int curX = width / 2;
        int curY = height / 2;

        for (MouseEventRecord ev : events) {
            curX = Math.max(0, Math.min(width - 1, curX + ev.dx()));
            curY = Math.max(0, Math.min(height - 1, curY + ev.dy()));

            int radius = ev.isButtonPress() ? 6 : 2;
            int intensity = ev.isButtonPress() ? 10 : 1;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int px = curX + dx;
                    int py = curY + dy;
                    if (px >= 0 && px < width && py >= 0 && py < height) {
                        density[px][py] += intensity;
                        if (density[px][py] > maxDensity) {
                            maxDensity = density[px][py];
                        }
                    }
                }
            }
        }

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int d = density[x][y];
                if (d > 0) {
                    float ratio = (float) d / maxDensity;
                    int alpha = Math.min(220, (int) (ratio * 255) + 35);
                    int r = (int) (Math.min(1.0f, ratio * 2.0f) * 255);
                    int g = (int) ((1.0f - Math.abs(ratio - 0.5f) * 2.0f) * 255);
                    int b = (int) ((1.0f - ratio) * 200);
                    int argb = (alpha << 24) | (r << 16) | (g << 8) | b;
                    img.setRGB(x, y, argb);
                }
            }
        }
        return img;
    }

    /**
     * Generates a heatmap directly from a .mousebin file.
     */
    public static BufferedImage generateFromFile(Path path, int width, int height) throws IOException {
        List<MouseEventRecord> events = MousebinCodec.readFromFile(path);
        return generate(events, width, height);
    }
}
