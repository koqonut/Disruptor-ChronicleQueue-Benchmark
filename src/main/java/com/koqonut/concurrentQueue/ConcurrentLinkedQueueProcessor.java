package com.koqonut.concurrentQueue;

import com.koqonut.Constants;
import com.koqonut.file.MyFileWriter;
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
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConcurrentLinkedQueueProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentLinkedQueueProcessor.class);


    @Param({"1048576"})
    public int queueSize;

    @Param({"4"})
    public int numReaders;

    @Param({"1", "4", "8", "16"})
    public int numWriters;


    public static void main(String[] args) throws IOException {
        //run from commandline
        //java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar com.koqonut.concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue
        org.openjdk.jmh.Main.main(args);

    }

    @Benchmark
    @Warmup(iterations = 0)
    @Measurement(iterations = 1)
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 0, jvmArgsAppend = {"-Xlog:gc*:out/gc_clq_16g.log:time,level,tags", "-Xms16g", "-Xmx16g", "-XX:+UseStringDeduplication"})
    public void benchmarkConcurrentLinkedQueue() throws ExecutionException, InterruptedException {

        MyFileWriter.printToFile(Constants.PERF_LQ, "-------" + queueSize + "-------" + Constants.RECORDS_TO_READ);

        StringBuilder sb = new StringBuilder();
        MyFileWriter.printToFile(Constants.PERF_LQ, "ConcurrentLinkedQueue Start time " + new Date());
        sb.append("records, qSize, numReaders, numWriters, Duration(ms)");
        MyFileWriter.printToFile(Constants.PERF_LQ, sb.toString());
        final Semaphore semaphore = new Semaphore(queueSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        ExecutorService producer = Executors.newFixedThreadPool(numReaders); // Create a thread pool with 2 threads
        ExecutorService consumer = Executors.newFixedThreadPool(numWriters); // Create a thread pool with 2 threads

        long startTime = System.currentTimeMillis();

        // Start producer and consumer threads
        Future<Boolean> res1 = producer.submit(new Producer(queue, Constants.CSV_INPUT_FILEPATH, Constants.RECORDS_TO_READ, semaphore));
        Future<Boolean> res2 = consumer.submit(new Consumer(queue, Constants.RECORDS_TO_READ, semaphore));

        res1.get();
        res2.get();

        // Shutdown the executor when tasks are complete
        producer.shutdown();
        consumer.shutdown();

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Processed {} records in {} milliseconds", Constants.RECORDS_TO_READ, duration);
        sb = new StringBuilder();
        sb.append(Constants.RECORDS_TO_READ).append(',').append(numReaders).append(',').append(numWriters).append(',').append(duration).append(',');

        MyFileWriter.printToFile(Constants.PERF_LQ, sb.toString());
        MyFileWriter.printToFile(Constants.PERF_LQ, "Total time taken in milliseconds " + duration);

    }
}
