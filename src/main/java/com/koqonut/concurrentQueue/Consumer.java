package com.koqonut.concurrentQueue;

import com.koqonut.Constants;
import com.koqonut.file.MyFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer implements Callable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private final ConcurrentLinkedQueue<String> queue;
    private final ConcurrentSkipListMap<String, Double> maxTemp = new ConcurrentSkipListMap<>();
    private final ConcurrentHashMap<String, Double> minTemp = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> sumTemp = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> countTemp = new ConcurrentHashMap<>();

    private final AtomicLong recordCount;
    private final Semaphore semaphore;

    public Consumer(ConcurrentLinkedQueue<String> queue, long numRecords, Semaphore semaphore) {
        this.queue = queue;
        this.recordCount = new AtomicLong(numRecords);
        this.semaphore = semaphore;
    }

    @Override
    public Boolean call() {
        logger.info("Started consumer to read {} records", recordCount.get());
        while (true) {
            //logger.info("Count is {}", count.get());

            if (recordCount.get() % (Constants.LOG_DISPLAYER) == 0) {
                logger.info("Consumer processed {} records", recordCount.get());
            }
            String data = queue.poll();
            if (data != null) {
                semaphore.release();
                recordCount.getAndDecrement();
                // Parse data (assuming format: city;temperature)
                String[] parts = data.split(Constants.DELIMITER);
                String city = parts[0].trim();

                double temperature = Double.parseDouble(parts[1].trim());

                maxTemp.put(city, Math.max(maxTemp.getOrDefault(city, Double.MIN_VALUE), temperature));
                minTemp.put(city, Math.min(minTemp.getOrDefault(city, Double.MAX_VALUE), temperature));
                countTemp.put(city, countTemp.getOrDefault(city, 0L) + 1);
                sumTemp.put(city, sumTemp.getOrDefault(city, 0.0) + temperature);
                if (recordCount.get() == 0) {
                    break;
                }
            }
        }

        logger.info("Queue processed {} ", Constants.RECORDS_TO_READ);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> e : maxTemp.entrySet()) {
            String city = e.getKey();
            sb.append(city).append(':')
                    .append(Math.round(minTemp.get(city) * 100.0) / 100.0).append('/')
                    .append(Math.round(sumTemp.get(city) / countTemp.get(city) * 100.0) / 100.0).append('/')
                    .append(Math.round(e.getValue() * 100.0) / 100.0);
            MyFileWriter.printToFile(Constants.CSV_OUTPUT_BQ, sb.toString());
            sb.setLength(0);
        }
        return recordCount.get() == 0;
    }


}
