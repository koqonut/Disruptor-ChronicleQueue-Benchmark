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

./mvnw clean verify

Create the measurements file with 1B rows (just once):
./create_measurements.sh 1000000000

Rename the file to measurements_1B.txt and move it src/main/data

Choose the proper constants and benchmark modes before running the code/

To simulate the benchmarking, we need a large dataset. You can generate a synthetic dataset using the provided data
generation script.

# Example command to run benchmarks

mvn clean install
java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar
java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar com.koqonut.disruptor.InputProcessor -o out/benchmark/result.txt
