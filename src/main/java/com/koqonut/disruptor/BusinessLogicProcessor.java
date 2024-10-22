package com.koqonut.disruptor;

import com.koqonut.Constants;
import com.koqonut.file.MyFileWriter;
import com.koqonut.model.CityStatisticsEvent;
import com.koqonut.model.CityTemperatureEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// Business logic handler to calculate statistics and publish to output disruptor
public class BusinessLogicProcessor implements EventHandler<CityTemperatureEvent> {

    private static final Logger logger = LoggerFactory.getLogger(InputProcessor.class);
    private final Map<String, Long> countMap = new HashMap<>();
    private final Map<String, Double> maxTemp = new HashMap<>();
    private final Map<String, Double> minTemp = new HashMap<>();
    private final Map<String, Double> sumTemp = new HashMap<>();
    private final String perfD;
    private RingBuffer<CityStatisticsEvent> outputRingBuffer;
    private int count = 0;
    private long startTime = 0;


    public BusinessLogicProcessor(RingBuffer<CityStatisticsEvent> outputRingBuffer, String perfFile) {
        this.outputRingBuffer = outputRingBuffer;
        perfD = perfFile;
    }

    private String city;
    private double temperature;

    public void processEvent(){
        if (count == 0) {
            startTime = System.currentTimeMillis();
            MyFileWriter.printToFile(perfD, "BL thread start time  " + new Date());
        }

        if (count % Constants.LOG_DISPLAYER == 0) {
            logger.info("Processing event no {}", count);
        }
        if (Constants.EOF.equals(city)) {
            // Publish to output disruptor
            long outputSequence = outputRingBuffer.next();
            try {
                CityStatisticsEvent cityStatsEvent = outputRingBuffer.get(outputSequence);
                cityStatsEvent.setCity(Constants.EOF);
                cityStatsEvent.setMinTemperature(0.0);
                cityStatsEvent.setMaxTemperature(0.0);
                cityStatsEvent.setSumTemperature(0.0);

            } finally {
                outputRingBuffer.publish(outputSequence);

            }
            long end = System.currentTimeMillis();
            long duration = end - startTime;
            logger.info("Total time in BL thread: {} milliseconds", duration);
            MyFileWriter.printToFile(perfD, "BL thread duration " + duration);
            double throughput = (double) count / duration * 1000.0;
            logger.info("Throughput is {} TPS, {}", throughput, count);

        } else {
            // Add temperature to city's list
            count++;
            maxTemp.put(city, Math.max(maxTemp.getOrDefault(city, 0.0), temperature));
            minTemp.put(city, Math.min(minTemp.getOrDefault(city, 0.0), temperature));
            sumTemp.put(city, sumTemp.getOrDefault(city, 0.0) + temperature);
            countMap.put(city, countMap.getOrDefault(city, 0L) + 1);

            // Publish to output disruptor
            long outputSequence = outputRingBuffer.next();
            try {
                CityStatisticsEvent cityStatsEvent = outputRingBuffer.get(outputSequence);
                cityStatsEvent.setCity(city);
                cityStatsEvent.setMinTemperature(minTemp.get(city));
                cityStatsEvent.setMaxTemperature(maxTemp.get(city));
                cityStatsEvent.setSumTemperature(sumTemp.get(city));
                cityStatsEvent.setNumOfReadings(countMap.get(city));

            } finally {
                outputRingBuffer.publish(outputSequence);

            }
        }
    }

    @Override
    public void onEvent(CityTemperatureEvent cityTemperatureEvent, long sequence, boolean endOfBatch) {
        city = cityTemperatureEvent.getCity();
        temperature = cityTemperatureEvent.getTemperature();
        processEvent();
    }
}

