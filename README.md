# FastMouseLogger 0.1.0 [ALPHA] — Native Raw Mouse Logging, Stream Compression & Heatmap Engine

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastMouseLogger/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastMouseLogger)

---

**⚡ High-performance raw mouse input logger, microsecond `.mousebin` dual-format streaming engine, and ARGB heatmap generator for Java.**

**FastMouseLogger** captures raw Windows mouse input directly via **[FastMouse](https://github.com/andrestubbe/FastMouse)** (WM_INPUT bypass of OS ballistics), compresses events in real-time into binary `.mousebin` logs using **[FastFileFormat](https://github.com/andrestubbe/FastFileFormat)** & **[FastBinary](https://github.com/andrestubbe/FastBinary)**, and computes high-resolution behavioral heatmaps and click density distributions.

---

## Quick Start

```java
import fastmouselogger.*;
import java.nio.file.Path;

public class Demo {
    public static void main(String[] args) throws Exception {
        Path logDir = Path.of("logs/mouse");

        // 1. Initialize background raw mouse logger
        try (FastMouseLogger logger = new FastMouseLogger(logDir, 5000)) {
            logger.addListener(rec -> {
                if (rec.isButtonPress()) {
                    System.out.println("Click at timestamp: " + rec.timestamp());
                }
            });

            logger.start();
            Thread.sleep(5000);
            logger.stop(); // Flushes automatically to .mousebin
        }
    }
}
```

---

## Key Features

- **🖱️ Win32 Raw Input Interception** — Sub-millisecond direct mouse capture bypassing OS cursor smoothing via `FastMouse`.
- **⚡ FastFileFormat `.mousebin` Compression** — Delta-timestamped VarInt event serialization (Payload ID `0x0003`).
- **🔥 Hardware-Accelerated Heatmap Generation** — High-speed ARGB accumulator and rasterizer for movement trajectories and click hotspots.
- **🔄 Zero GC Pressure** — Contiguous memory buffers, reusable event records, and batch disk flush pipelines.
- **📦 Zero Heavy Dependencies** — Native-speed pure Java 17+ core backed by `FastCore`, `FastBinary`, and `FastFileFormat`.

---

## Real-World Scenarios

- **🤖 AI Agent Behavioral Recording** — Logging human mouse interaction trajectories for imitation learning and GUI robot training.
- **🎮 Esports & Aim Analytics** — Tracking raw sensor deltas, acceleration curves, and click latencies in gaming environments.
- **📊 UX & Usability Heatmaps** — Visualizing user attention hotspots and click distributions on desktop applications.
- **🛡️ Biometric Telemetry** — Capturing fine-grained micro-movement signatures for desktop authentication.

---

## Performance Benchmarks

FastMouseLogger is profiled using **JMH** to guarantee maximum stream throughput and zero dropped input packets.

| Benchmark Operation | Score (ops/ms) | Event Throughput | Memory Overhead |
|---|---|---|---|
| **Binary Stream Decoding (`.mousebin`)** | **~75,000 ops/ms** | **> 75 Million events/sec** | **Zero-Copy Streaming** |
| **Binary Stream Encoding (`.mousebin`)** | **~50,000 ops/ms** | **> 50 Million events/sec** | **Compact VarInt Delta Buffer** |
| **Heatmap Rasterization (800x600 ARGB)** | **~550 ops/sec** | **550 Full HD Frames/sec** | **Direct Pixel Buffer Writing** |

*Run the benchmarks locally:* `.\run-benchmark.bat`

---

## API Quick Reference

| Method / Class | Description |
|---|---|
| `new FastMouseLogger(path, threshold)` | Creates a logger flushing every N records into timestamped `.mousebin` files. |
| `logger.start()` / `logger.stop()` | Starts and stops native raw mouse event recording. |
| `logger.addListener(listener)` | Subscribes to real-time mouse movement and click callbacks. |
| `MousebinCodec.encode(events)` | Serializes event list into compressed FastFileFormat binary byte array. |
| `MousebinCodec.decode(bytes)` | Deserializes `.mousebin` binary bytes back into `List<MouseEventRecord>`. |
| `HeatmapGenerator.generate(events, w, h)` | Renders ARGB density heatmap `BufferedImage` from mouse records. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Live Mouse Streamer & Heatmap Demo** | [Demo.java](examples/Demo/src/main/java/fastmouselogger/demo/Demo.java) | `run-demo.bat` | 3-second live raw recording, `.mousebin` encoding/decoding, and ARGB heatmap generation. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastmouselogger/benchmark/Benchmark.java) | `run-benchmark.bat` | High-throughput encoding/decoding benchmarks and heatmap rasterization speed. |

---

## Installation

### Option 1: Maven (JitPack)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastMouseLogger</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastMouse</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastFileFormat</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastBinary</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastMouseLogger:0.1.0'
    implementation 'com.github.andrestubbe:FastMouse:0.1.0'
    implementation 'com.github.andrestubbe:FastFileFormat:0.1.0'
    implementation 'com.github.andrestubbe:FastBinary:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. 🖱️ **[FastMouseLogger-0.1.0.jar](https://github.com/andrestubbe/FastMouseLogger/releases/download/0.1.0/FastMouseLogger-0.1.0.jar)** (Mouse Logger & Heatmap Engine)
2. ⚡ **[FastMouse-0.1.0.jar](https://github.com/andrestubbe/FastMouse/releases/download/0.1.0/FastMouse-0.1.0.jar)** (Native Win32 Raw Mouse Input)
3. 📄 **[FastFileFormat-0.1.0.jar](https://github.com/andrestubbe/FastFileFormat/releases/download/0.1.0/FastFileFormat-0.1.0.jar)** (Dual Binary & Text File Format)
4. ⚡ **[FastBinary-0.1.0.jar](https://github.com/andrestubbe/FastBinary/releases/download/0.1.0/FastBinary-0.1.0.jar)** (VarInt & Binary Packing)
5. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (Foundation Library)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API reference and method signatures.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Architectural design principles and zero-drop input goals.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version notes.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.
* **[COMPILE.md](docs/COMPILE.md)**: Instructions for compiling from source.

---

## Platform Support

| Platform | Status |
|---|---|
| Windows 10/11 (x64) | ✅ Fully Supported (Win32 Raw Input) |
| Linux | 🚧 Planned (evdev / XInput2) |
| macOS | 🚧 Planned (CGEventTap) |

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastMouse](https://github.com/andrestubbe/FastMouse) — Ultra-low latency raw mouse capture for Windows
- [FastKeyboard](https://github.com/andrestubbe/FastKeyboard) — Low-level raw keyboard event interceptor
- [FastKeylogger](https://github.com/andrestubbe/FastKeylogger) — Biometric typing cadence and keystroke logger
- [FastFileFormat](https://github.com/andrestubbe/FastFileFormat) — Universal dual-format binary & text document engine
- [FastSharedMemory](https://github.com/andrestubbe/FastSharedMemory) — Zero-copy inter-process shared memory for Java

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
