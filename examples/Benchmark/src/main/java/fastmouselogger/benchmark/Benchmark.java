package fastmouselogger.benchmark;

import fastmouselogger.HeatmapGenerator;
import fastmouselogger.MouseEventRecord;
import fastmouselogger.MousebinCodec;
import org.openjdk.jmh.annotations.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class Benchmark {

    private List<MouseEventRecord> sampleEvents;
    private byte[] sampleBinary;

    @Setup
    public void setup() {
        sampleEvents = new ArrayList<>(1000);
        long baseTime = 1770000000000L;
        for (int i = 0; i < 1000; i++) {
            int flag = (i % 50 == 0) ? MouseEventRecord.FLAG_LEFT_DOWN : MouseEventRecord.FLAG_MOVE;
            sampleEvents.add(new MouseEventRecord(baseTime + (i * 8L), flag, (i % 7) - 3, (i % 5) - 2, 0));
        }
        sampleBinary = MousebinCodec.encode(sampleEvents);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkEncode1000Events() {
        return MousebinCodec.encode(sampleEvents);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public List<MouseEventRecord> benchmarkDecode1000Events() {
        return MousebinCodec.decode(sampleBinary);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public BufferedImage benchmarkHeatmapRasterization() {
        return HeatmapGenerator.generate(sampleEvents, 800, 600);
    }
}
