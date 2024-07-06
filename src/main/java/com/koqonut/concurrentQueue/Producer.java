package com.koqonut.concurrentQueue;

import com.koqonut.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class Producer implements Callable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    private final ConcurrentLinkedQueue<String> queue;
    private final String filename;
    private final AtomicLong numOfRecords;
    private final Semaphore semaphore;

    public Producer(ConcurrentLinkedQueue<String> queue, String filename, long numRecords, Semaphore semaphore) {
        this.queue = queue;
        this.filename = filename;
        this.numOfRecords = new AtomicLong(numRecords);
        this.semaphore = semaphore;
    }

    @Override
    public Boolean call() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while (numOfRecords.getAndDecrement() > 0 && (line = reader.readLine()) != null) {

                if (numOfRecords.get() % Constants.LOG_DISPLAYER == 0) {
                    logger.info("Producer processed {} records", numOfRecords.get());
                }
                semaphore.acquire();
                queue.offer(line); // Add line to the queue, wait if it is full

            }
        } catch (IOException|InterruptedException e) {
            logger.error("Error encountered {}", e.getMessage());
        }
        logger.info("Published all records ");

        return numOfRecords.get() == 0;
    }
}
