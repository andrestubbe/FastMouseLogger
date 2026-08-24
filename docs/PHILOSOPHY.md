# FastMouseLogger Philosophy

> [!IMPORTANT]
> **"Zero Input Lag. Zero Dropped Frames. Lossless Binary Telemetry."**

Traditional GUI event capture models (AWT/Swing MouseListeners or polling loops) introduce heavy OS acceleration bias, drop fine-grained sub-millisecond movements, and suffer from garbage collection jitter.

`FastMouseLogger` couples direct Win32 Raw Input capture (`FastMouse`) with lock-free memory buffers and compact VarInt delta streaming (`FastFileFormat`), allowing AI agents and telemetry pipelines to capture millions of precision events per second with zero OS interference.
