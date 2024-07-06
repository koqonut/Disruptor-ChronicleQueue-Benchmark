package com.koqonut.disruptor;

import com.koqonut.Constants;
import com.koqonut.cq.CQWriter;
import com.koqonut.factory.CityStatisticsEventFactory;
import com.koqonut.factory.CityTemperatureEventFactory;
import com.koqonut.file.MyFileReader;
import com.koqonut.file.MyFileWriter;
import com.koqonut.model.CityStatisticsEvent;
import com.koqonut.model.CityTemperatureEvent;
import com.lmax.disruptor.dsl.Disruptor;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class InputProcessor {
    private static final int COUNT = Constants.RECORDS_TO_READ;
    private static final Logger logger = LoggerFactory.getLogger(InputProcessor.class);

    @Param({"4096","8192", "16384", "32768", "131072"})
    public int ringBufferSize;

    @Param({"false"})
    public boolean shouldJournal;

    public static void main(String[] args) throws IOException {
        // org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    @Warmup(iterations = 0)
    @Measurement(iterations = 1)
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 0, jvmArgsAppend = {"-Xlog:gc*:out/gc_d.log:time,level,tags","-Xms4g","-Xmx4g","-XX:+UseStringDeduplication"})
    public void benchmarkDisruptor() throws ExecutionException, InterruptedException {
        MyFileWriter.printToFile(Constants.PERF_D, "-------------" + ringBufferSize + "-----------");

        // Executor to handle threads
        // Define a ThreadFactory to create threads for disruptor
        ThreadFactory businessLogicThreadFactory = r -> new Thread(r, "BL");

        CQWriter inputJournaler = new CQWriter(Constants.CQ_INPUT_PATH);
        CQWriter outputJournaler = new CQWriter(Constants.CQ_BL_PATH);

        ThreadFactory outputThreadFactory = r -> new Thread(r, "OUT");

        ThreadFactory inputThreadFactory = r -> new Thread(r, "IN");

        ExecutorService executor = Executors.newFixedThreadPool(1, inputThreadFactory);


        // Disruptor setup for input, output
        Disruptor<CityTemperatureEvent> inputDisruptor = new Disruptor<>(new CityTemperatureEventFactory(),
                ringBufferSize, businessLogicThreadFactory);
        Disruptor<CityStatisticsEvent> outputDisruptor = new Disruptor<>(new CityStatisticsEventFactory(),
                ringBufferSize, outputThreadFactory);

        // Connect disruptors
        inputDisruptor.handleEventsWith(new BusinessLogicProcessor(outputDisruptor.getRingBuffer(), Constants.PERF_D, shouldJournal, outputJournaler));
        outputDisruptor.handleEventsWith(new OutputProcessor(Constants.CSV_OUTPUT_DIS, Constants.PERF_D));

        // Start disruptors
        inputDisruptor.start();
        outputDisruptor.start();


        long startTime = System.currentTimeMillis();
        logger.info("Start time {}", System.currentTimeMillis());
        // Read from file and publish to input disruptor
        Future<Boolean> done = executor.submit(new MyFileReader(inputDisruptor, Constants.CSV_INPUT_FILEPATH, COUNT, shouldJournal, inputJournaler));

        done.get();
        long duration = System.currentTimeMillis() - startTime;
        StringBuilder sb = new StringBuilder();
        sb.append(ringBufferSize).append(',')
                .append(duration).append(',')
                .append("Main Start time: ").append(startTime).append(';');
        MyFileWriter.printToFile(Constants.PERF_D, sb.toString());

        // Shutdown
        logger.info("End time {}", System.currentTimeMillis());
        MyFileWriter.printToFile(Constants.PERF_D, "Main duration " + duration);


        inputDisruptor.shutdown();
        outputDisruptor.shutdown();
        executor.shutdown();
    }

}