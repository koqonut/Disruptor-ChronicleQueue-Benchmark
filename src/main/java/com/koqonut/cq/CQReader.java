package com.koqonut.cq;

import com.koqonut.generated.CityTempDecoder;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class CQReader {
    private static final Logger logger = LoggerFactory.getLogger(CQReader.class);

    private static final int BUFFER_SIZE = 2048; // Example buffer size

    private final byte[] arr;

    private final String cqPath;

    public CQReader(String cqPath) {
        this.cqPath = cqPath;
        arr = new byte[BUFFER_SIZE];
    }

    public void read() {
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.single(cqPath)
                .rollCycle(RollCycles.FAST_DAILY)
                .build()) {
            ExcerptTailer tailer = queue.createTailer();
            while (true) {
                if (!tailer.readBytes(b -> {
                    int val = (int) b.readRemaining();
                    b.read(arr, 0, val);
                    UnsafeBuffer directBuffer = new UnsafeBuffer(arr);
                    CityTempDecoder cityTemperatureDecoder = new CityTempDecoder();
                    cityTemperatureDecoder.wrap(directBuffer, 0, val, 0);
                    logger.info("Data successfully read from Chronicle Queue: {}", cityTemperatureDecoder);
                    Arrays.fill(arr, (byte) 0);
                })) {
                    break; // Exit the loop when no more data is available
                }
            }
        }
    }
}
