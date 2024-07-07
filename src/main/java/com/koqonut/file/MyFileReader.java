package com.koqonut.file;

import com.koqonut.Constants;
import com.koqonut.cq.CQWriter;
import com.koqonut.model.CityTemperatureEvent;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

public class MyFileReader implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(MyFileReader.class);

    private final Disruptor<CityTemperatureEvent> inputDisruptor;
    private final String filePath;
    private final int recordCount;

    private final boolean shouldJournal;
    private final CQWriter cqWriter;

    public MyFileReader(Disruptor<CityTemperatureEvent> inputDisruptor, String filePath, int recordCount, boolean shouldJournal, CQWriter cqWriter) {
        this.inputDisruptor = inputDisruptor;
        this.filePath = filePath;
        this.recordCount = recordCount;
        this.shouldJournal = shouldJournal;
        this.cqWriter = cqWriter;
    }

    @Override
    public Boolean call() {
        logger.info("Starting to read {} from file ", recordCount);
        long start = System.currentTimeMillis();

        int records = recordCount;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while (records > 0 && (line = reader.readLine()) != null) {

                if (records % Constants.LOG_DISPLAYER == 0) {
                    logger.info("Read file {}", (recordCount - records));
                }
                String[] parts = line.split(";");
                String city = parts[0].trim();
                double temperature = Double.parseDouble(parts[1].trim());

                long sequence = inputDisruptor.getRingBuffer().next();
                try {
                    CityTemperatureEvent event = inputDisruptor.getRingBuffer().get(sequence);
                    event.setCity(city);
                    event.setTemperature(temperature);
                } finally {
                    inputDisruptor.getRingBuffer().publish(sequence);
                    if (shouldJournal) {
                        cqWriter.write(line);
                    }
                }
                records--;
            }
        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage());
        }
        long end = System.currentTimeMillis();
        logger.info("Read {} records  in  {} milliseconds", recordCount, (end - start));

        //reading complete....send end event
        long sequence = inputDisruptor.getRingBuffer().next();
        try {
            CityTemperatureEvent event = inputDisruptor.getRingBuffer().get(sequence);
            event.setCity(Constants.EOF);
            event.setTemperature(0.0);
        } finally {
            inputDisruptor.getRingBuffer().publish(sequence);

        }
        if (shouldJournal) {
            cqWriter.write(Constants.EOF + ";" + 0.0);
            cqWriter.close();
        }
        return true;
    }


}
