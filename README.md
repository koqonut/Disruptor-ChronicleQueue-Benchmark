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
- **src/main/data**: Contains the input data file (`city_temperatures.txt`).

## Prerequisites

- Java 8 or higher
- Maven
- AWS CLI (for EC2 instance setup)
- JMH (Java Microbenchmark Harness)

## Setup

### Data Generation

A python script is provided in src/main/data one parameter which 
specifies the number of lines of city,temperature reading to generate in city_temperatures.txt.
Use it to create a billion lines in a single file.

```
python generate_city_temperatures.py 1000000000

```

Choose the proper constants and benchmark modes before running the code/

To simulate the benchmarking, we need a large dataset. You can generate a synthetic dataset using the provided data
generation script.

### Example command to run benchmarks

```

mvn clean install

# run all test
java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar

# run single test
java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar com.koqonut.disruptor.InputProcessor -o out/benchmark/result.txt

```

# Benchmark results

### Environment details

```
AWS Instance details t3.2xlarge   8vCPU 	32 GB RAM

VM version: JDK 21.0.3, OpenJDK 64-Bit Server VM

21.0.3+9-LTS

VM options: -Xlog:gc*:out/gc_16g.log:time,level,tags -Xms16g -Xmx16g -XX:+UseStringDeduplication

```




### Impact of ring buffer size for reading 1 Billion lines from a dile in disk

|         Benchmark          |    ringBufferSize    | Avg time Score (ms/op) | Units |
|:--------------------------:|:--------------------:|:----------------------:|----------------------:|
|       BlockingQueue        |        65536         |       642222.574       | ms/op |
|       BlockingQueue        |        131072        |       672457.457       |ms/op |
|   ConcurrentLinkedQueue    |        262144        |       641021.406       |ms/op |
|   ConcurrentLinkedQueue    |        524288        |       701570.130       |ms/op |
|         Disruptor          |      1048576/A       |       777010.755       |ms/op |
|        Singlethread        |       2097152        |       821035.036       |ms/op |



#### Time taken to read 1 Billion lines from a file in disk using Array blocking queue, concurrent linked queue , disruptor and Single threaded process

| Benchmark                                                                       | (numReaders) | (numWriters) | (queueSize) | (ringBufferSize| Mode  | Cnt         | Score  | Error  | Units   |
|---------------------------------------------------------------------------------|--------------|--------------|-------------|--------|--------|-------------|--------|--------|---------|
| blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue                      | 4            | 1            | 1048576     | N/A|avgt   | 1847657.230 |        |        | ms/op   |
| blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue                      | 4            | 4            | 1048576     |  N/A|avgt   | 1891556.671 |        |        | ms/op   |
| blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue                      | 4            | 8            | 1048576     |  N/A|avgt   | 1896590.471 |        |        | ms/op   |
| blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue                      | 4            | 16           | 1048576     |  N/A|avgt   | 1893679.263 |        |        | ms/op   |
| concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue    | 4            | 1            | 1048576     |  N/A|avgt   | 1623709.735 |        |        | ms/op   |
| concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue    | 4            | 4            | 1048576     | N/A| avgt   | 1587018.099 |        |        | ms/op   |
| concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue    | 4            | 8            | 1048576     |  N/A|avgt   | 1582008.802 |        |        | ms/op   |
| concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue    | 4            | 16           | 1048576     |  N/A|avgt   | 1626868.889 |        |        | ms/op   |
| singlethread.SingleThreadSolver.process                                          | N/A          | N/A          | N/A         |  N/A|avgt   | 761912.831  |        |        | ms/op   |
|InputProcessor.benchmarkDisruptor                                                 |     N/A      |   N/A        |       N/A   |  131072    |avgt| 707242.338  | |   | ms/op|


# Conclusion

From the benchmark table provided, several conclusions can be drawn based on the performance metrics  of different queue implementations across varying numbers of readers and writers:

### Comparison of Queue Implementations:

#### BlockingQueue vs ConcurrentLinkedQueue:
Across different configurations of readers and writers, ConcurrentLinkedQueue generally shows lower average times (us/op) compared to BlockingQueue.
This suggests that for scenarios with multiple readers and writers (numReaders and numWriters > 1), ConcurrentLinkedQueue may provide better performance due to its non-blocking characteristics.
Scaling with Readers and Writers:

Impact of Scalability:
As the number of readers and writers increases (numReaders and numWriters), the average operation time tends to increase for both queue implementations.
This indicates potential contention and overheads associated with concurrent access in both BlockingQueue and ConcurrentLinkedQueue under higher load scenarios.
Single Threaded Performance:

#### SingleThreadSolver:
The single-threaded approach shows significantly lower operation times (us/op) compared to multi-threaded approaches using queues.
This highlights the overhead introduced by concurrent processing and synchronization in multi-threaded scenarios.
Choosing the Right Queue:

#### Disruptor 

Disruptor outperforms BlockingQueue vs ConcurrentLinkedQueue implying that avoiding locks can drastically improve performance


# Next steps

Optimize for garbage collection by pre allocation objects.
Using Simple Binary Encoding serialization to transfer data between thread.

---------------------------------------------------------------------------------------------


### Raw  Data


Benchmark                          (ringBufferSize)  (shouldJournal)  Mode  Cnt          Score   Error  Units
InputProcessor.benchmarkDisruptor             65536            false  avgt    2  642222574.400          us/op
InputProcessor.benchmarkDisruptor            131072            false  avgt    2  672457457.811          us/op
InputProcessor.benchmarkDisruptor            262144            false  avgt    2  641021406.430          us/op
InputProcessor.benchmarkDisruptor            524288            false  avgt    2  701570130.256          us/op
InputProcessor.benchmarkDisruptor           1048576            false  avgt    2  777010755.236          us/op
InputProcessor.benchmarkDisruptor           2097152            false  avgt    2  821035036.658          us/op

Benchmark                                                                      (numReaders)  (numWriters)  (queueSize)  Mode  Cnt           Score   Error  Units
blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue                               4             1      1048576  avgt       1847657230.663          us/op
blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue                               4             4      1048576  avgt       1891556671.612          us/op
blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue                               4             8      1048576  avgt       1896590471.312          us/op
blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue                               4            16      1048576  avgt       1893679263.733          us/op
concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue             4             1      1048576  avgt       1623709735.610          us/op
concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue             4             4      1048576  avgt       1587018099.355          us/op
concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue             4             8      1048576  avgt       1582008802.037          us/op
concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue             4            16      1048576  avgt       1626868889.970          us/op
singlethread.SingleThreadSolver.process                                                 N/A           N/A          N/A  avgt        761912831.567          us/op

Benchmark                                                                      (numReaders)  (numWriters)  (
queueSize)  (ringBufferSize)  (shouldJournal)  Mode Cnt Score Error Units
blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue 4 1 1048576 N/A N/A avgt 1823453.569 ms/op
blockingQueue.BlockingQueueProcessor.benchmarkBlockingQueue 4 4 1048576 N/A N/A avgt 1814973.933 ms/op
concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue 4 1 1048576 N/A N/A avgt 1633299.596 ms/op
concurrentQueue.ConcurrentLinkedQueueProcessor.benchmarkConcurrentLinkedQueue 4 4 1048576 N/A N/A avgt 1521605.200 ms/op
disruptor.InputProcessor.benchmarkDisruptor N/A N/A N/A 131072 false avgt 707242.338 ms/op
singlethread.SingleThreadSolver.process N/A N/A N/A N/A N/A avgt 765399.803 ms/op

|Benchmark                   | (numReaders) | (numWriters) |   (queueSize)  | (ringBufferSize)  | Avg time Score (ms/op)|
|:--------------------------:|:------------:|-------------:|:--------------:|:-----------------:|----------------------:|
|BlockingQueue               |     4        |   1          |    1048576     |      N/A          |   1823453.569 ms/op   |
|BlockingQueue               |     4        |   4          |    1048576     |      N/A          |   1814973.933 ms/op   |
|ConcurrentLinkedQueue       |     4        |   1          |    1048576     |      N/A          |   1633299.596 ms/op   |
|ConcurrentLinkedQueue       |     4        |   4          |    1048576     |      N/A          |   1521605.2   ms/op   |
|Disruptor                   |     N/A      |   N/A        |       N/A      |      131072       |   707242.338  ms/op   |
|Singlethread                |     N/A      |   N/A        |        N/A     |      N/A          |   765399.803  ms/op   |




