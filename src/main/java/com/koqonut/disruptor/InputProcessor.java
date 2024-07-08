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
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class InputProcessor {
    private static final int COUNT = Constants.RECORDS_TO_READ;
    private static final Logger logger = LoggerFactory.getLogger(InputProcessor.class);

    @Param({"65536", "131072", "262144", "524288",  "1048576", "2097152"})
    public int ringBufferSize;

    @Param({"false"})
    public boolean shouldJournal;

    public static void main(String[] args) throws IOException {
        // org.openjdk.jmh.Main.main(args);
        //java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar com.koqonut.disruptor.InputProcessor -o out/benchmark/d_8g.txt
    }

    @Benchmark
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 0, jvmArgsAppend = {"-Xlog:gc*:out/gc_d_16g.log:time,level,tags", "-Xms16g", "-Xmx16g", "-XX:+UseStringDeduplication"})
    public void benchmarkDisruptor() throws ExecutionException, InterruptedException {
        MyFileWriter.printToFile(Constants.PERF_D, "-------" + ringBufferSize + "-------" + Constants.RECORDS_TO_READ);

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
        inputDisruptor.handleEventsWith(new BusinessLogicProcessor(outputDisruptor.getRingBuffer(), Constants.PERF_D));
        outputDisruptor.handleEventsWith(new OutputProcessor(Constants.CSV_OUTPUT_DIS, Constants.PERF_D, outputJournaler,shouldJournal));

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