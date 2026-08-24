# Changelog: FastMouseLogger

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-08-24
### Added
- **Native Raw Mouse Logger (`FastMouseLogger`)**: Win32 Raw Input interception bypassing Windows ballistics.
- **FastFileFormat Binary Streamer (`MousebinCodec`)**: Real-time VarInt timestamp delta compression (`.mousebin` Payload 0x0003).
- **ARGB Heatmap Generator (`HeatmapGenerator`)**: Real-time density accumulator for trajectory visualization and click hotspots.
- **Interactive Showcase & JMH Benchmark Suite**: Profiling >75M events/sec decoding and >50M events/sec encoding.
