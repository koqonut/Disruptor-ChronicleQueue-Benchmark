# Disruptor vs ArrayBlockingQueue vs ConcurrentLinkedQueue Benchmark

This project benchmarks the performance of the LMAX Disruptor against ArrayBlockingQueue and ConcurrentLinkedQueue. The
benchmarking task involves reading a file containing 1 billion records of city and temperature data, and calculating the
minimum, maximum, and average temperature for each city.

## Overview

The goal of this project is to compare the performance of three different concurrency frameworks:

- **LMAX Disruptor**
- **ArrayBlockingQueue**
- **ConcurrentLinkedQueue**
- **Single Thread Implementation**

By processing a large dataset in parallel, we aim to determine which framework offers the best performance in terms of
throughput and latency.

## Project Structure

- **src/main/java**: Contains the main implementation files.
    - **disruptor**: Contains the Disruptor implementation.
    - **arrayblockingqueue**: Contains the ArrayBlockingQueue implementation.
    - **concurrentlinkedqueue**: Contains the ConcurrentLinkedQueue implementation.
- **src/main/data**: Contains the input data file (`measurements.txt`).

## Prerequisites

- Java 8 or higher
- Maven
- AWS CLI (for EC2 instance setup)
- JMH (Java Microbenchmark Harness)

## Setup

### Data Generation

Use [1BRC project](https://github.com/gunnarmorling/1brc) to generate the file with 1 billion records and place it in
src/main/data

Build the 1BRC project using Apache Maven:

```
./mvnw clean verify

Create the measurements file with 1B rows (just once):
./create_measurements.sh 1000000000
```

Rename the file to measurements_1B.txt and move it src/main/data

Choose the proper constants and benchmark modes before running the code/

To simulate the benchmarking, we need a large dataset. You can generate a synthetic dataset using the provided data
generation script.

# Example command to run benchmarks

```

mvn clean install

# run all test
java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar

# run single test
java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar com.koqonut.disruptor.InputProcessor -o out/benchmark/result.txt

```

# Benchmark results

```


 JMH version: 1.37	ABQ (4R, 1W)    1823453.569

 VM version: JDK 21.0.3, OpenJDK 64-Bit Server VM, 21.0.3+9-LTS	ABQ (4R, 4W)    1814973.933

 AWS Instance details t3.2xlarge   8vCPU 	32 GB RAM


 VM invoker: /home/ec2-user/.sdkman/candidates/java/21.0.3-amzn/bin/java	CLQ (4R, 1W)    1633299.596    [1048576 queueLimit]

 VM options: -Xlog:gc*:out/gc_bq_16g.log:time,level,tags -Xms16g -Xmx16g -XX:+UseStringDeduplication	CLQ (4R, 4W)    1521605.2    [1048576 queueLimit]

Benchmark                                                                      (numReaders)  (numWriters)  (
queueSize)  (ringBufferSize)  (shouldJournal)  Mode Cnt Score Error Units
blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue 4 1 1048576 N/A N/A avgt 1823453.569 ms/op
blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue 4 4 1048576 N/A N/A avgt 1814973.933 ms/op
concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue 4 1 1048576 N/A N/A avgt 1633299.596 ms/op
concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue 4 4 1048576 N/A N/A avgt 1521605.200 ms/op
disruptor.InputProcessor.benchmarkDisruptor N/A N/A N/A 131072 false avgt 707242.338 ms/op
singlethread.SingleThreadSolver.process N/A N/A N/A N/A N/A avgt 765399.803 ms/op

```