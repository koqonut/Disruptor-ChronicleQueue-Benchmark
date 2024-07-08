package com.koqonut.disruptor;

import com.koqonut.Constants;
import com.koqonut.cq.CQWriter;
import com.koqonut.file.MyFileWriter;
import com.koqonut.model.CityStatisticsEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TreeMap;

// Output handler to store statistics and print to file
class OutputProcessor implements EventHandler<CityStatisticsEvent> {
    public static final char SLASH = '/';
    public static final char COMMA = ',';
    public static final char EQUALS = '=';
    private static final Logger logger = LoggerFactory.getLogger(OutputProcessor.class);
    private final String outputFile;
    private final String perfFile;
    private final CQWriter outputJournaler;
    private final TreeMap<String, CityStatisticsEvent> cityStatisticsMap = new TreeMap<>();
    long startTime = 0;
    private int count = 0;
    private final boolean shouldJournal;

    public OutputProcessor(String outputFile, String perfFile, CQWriter outputJournaler, boolean shouldJournal) {
        this.outputFile = outputFile;
        this.perfFile = perfFile;

        this.outputJournaler = outputJournaler;
        this.shouldJournal = shouldJournal;
    }

    @Override
    public void onEvent(CityStatisticsEvent cityStatisticsEvent, long sequence, boolean endOfBatch) {
        String city = cityStatisticsEvent.getCity();
        StringBuilder sb = new StringBuilder();
        if (Constants.EOF.equalsIgnoreCase(city)) {


            for (CityStatisticsEvent stats : cityStatisticsMap.values()) {
                double avg = Math.round((stats.getSumTemperature() / stats.getNumOfReadings() * 100.0) / 100.0);
                double min = Math.round((stats.getMinTemperature() * 100.0) / 100.0);
                double max = Math.round((stats.getMaxTemperature() * 100.0) / 100.0);
                sb.append(stats.getCity()).append(EQUALS)
                        .append(min)
                        .append(SLASH)
                        .append(avg)
                        .append(SLASH)
                        .append(max)
                        .append(COMMA);
                MyFileWriter.printToFile(outputFile, sb.toString());
                sb.setLength(0);
            }

            long stopTime = System.currentTimeMillis();

            MyFileWriter.printToFile(perfFile, "OL Stop time: " + stopTime + ';');
            long duration = stopTime - startTime;
            MyFileWriter.printToFile(perfFile, "OL Duration (ms): " + duration + ';');

            outputJournaler.close();

            logger.debug("Total time taken by OP_Thread {} milliseconds", (stopTime - startTime));

        } else {
            cityStatisticsMap.put(city, cityStatisticsEvent);
            if(shouldJournal){
                outputJournaler.write(cityStatisticsEvent.toString());
            }

            if (count == 0) {
                startTime = System.currentTimeMillis();
                MyFileWriter.printToFile(perfFile, "OL Start time: " + new Date() + ';');
            }

            if (count % Constants.LOG_DISPLAYER == 0) {
                logger.info("Output thread processing event no {}", count);
            }
            count++;
        }
    }
}
