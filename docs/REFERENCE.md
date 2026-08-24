# FastMouseLogger API Reference

## Core Classes

### 1. `fastmouselogger.FastMouseLogger`
* `public FastMouseLogger(Path outputDirectory, int bufferFlushThreshold)`: Creates background logger instance.
* `public void start()`: Starts raw mouse input thread.
* `public void stop()`: Stops capture and flushes pending buffer.
* `public void addListener(MouseEventListener listener)`: Registers event callback.
* `public void removeListener(MouseEventListener listener)`: Unregisters callback.
* `public List<MouseEventRecord> getBufferedRecords()`: Returns current memory buffer snapshot.
* `public void flush()`: Writes memory buffer to timestamped `.mousebin` file.

### 2. `fastmouselogger.MousebinCodec`
* `public static byte[] encode(List<MouseEventRecord> events)`: Encodes events to FastFileFormat binary stream.
* `public static List<MouseEventRecord> decode(byte[] bytes)`: Decodes `.mousebin` binary payload.
* `public static void writeToFile(Path path, List<MouseEventRecord> events)`: Writes events to file.
* `public static List<MouseEventRecord> readFromFile(Path path)`: Reads events from file.

### 3. `fastmouselogger.HeatmapGenerator`
* `public static BufferedImage generate(List<MouseEventRecord> events, int width, int height)`: Renders ARGB heatmap.
* `public static BufferedImage generateFromFile(Path path, int width, int height)`: Renders heatmap from file.
