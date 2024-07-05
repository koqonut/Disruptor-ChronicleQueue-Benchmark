package com.koqonut.cq;

import com.koqonut.generated.CityTempEncoder;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CQWriter {


    private static final int BUFFER_SIZE = 256; // Example buffer size

    private final byte[] arr;
    private final String cqPath;

    public CQWriter(String cqPath) {
        this.arr = new byte[BUFFER_SIZE];
        this.cqPath = cqPath;
    }

    public void write(String city, double temperature) {

        // Encode data using SBE
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        CityTempEncoder cityTemperatureEncoder = new CityTempEncoder();
        cityTemperatureEncoder.wrap(directBuffer, 0)
                .city(city)
                .temperature(temperature);

        int encodedLength = cityTemperatureEncoder.encodedLength();
        directBuffer.getBytes(0, arr, 0, encodedLength);

        // Write data to Chronicle Queue
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.single(cqPath)
                .rollCycle(RollCycles.FIVE_MINUTELY)
                .build()) {

            ExcerptAppender appender = queue.acquireAppender();
            appender.writeBytes(b -> b.write(arr));

            System.out.println("Data successfully written to Chronicle Queue: " + cityTemperatureEncoder.toString());
        }

        Arrays.fill(arr, (byte) 0);
    }
}
