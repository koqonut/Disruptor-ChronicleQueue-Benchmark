package com.koqonut.cq;

import com.koqonut.Constants;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.util.Date;

public class CQWriter {


    private static final int BUFFER_SIZE = 256; // Example buffer size


    private final String cqPath;
    SingleChronicleQueue queue;

    public CQWriter(String cqPath) {
        this.cqPath = cqPath;
        queue = SingleChronicleQueueBuilder.single(cqPath)
                .rollCycle(RollCycles.FIVE_MINUTELY)
                .build();
    }

    public void write(String data) {

        // Write data to Chronicle Queue
        ExcerptAppender appender = queue.acquireAppender();
        appender.writeBytes(b -> b.write(data));
    }


    public void close() {
        queue.close();
    }

    public static void main(String[] args) {
        // Write data to Chronicle Queue
        CQWriter cqWriter = new CQWriter(Constants.CQ_INPUT_PATH);

        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.single(cqWriter.cqPath)
                .rollCycle(RollCycles.FIVE_MINUTELY)
                .build()) {

            ExcerptAppender appender = queue.acquireAppender();
            appender.writeBytes(b -> b.write("Hello " + new Date()));

            System.out.println("Data successfully written to Chronicle Queue: ");
        }

    }
}
