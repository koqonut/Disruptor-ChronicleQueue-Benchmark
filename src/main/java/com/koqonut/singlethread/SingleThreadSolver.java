/*
 *  Copyright 2023 The original authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.koqonut.singlethread;

import com.koqonut.Constants;
import com.koqonut.file.MyFileWriter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SingleThreadSolver {

    private static final String FILE = Constants.CSV_INPUT_FILEPATH;
    private static final Logger logger = LoggerFactory.getLogger(SingleThreadSolver.class);

    SingleThreadSolver singleThreadSolver;


    public static void main(String[] args) {
        //java -jar target/Chronicle-Queue-1.0-SNAPSHOT.jar com.koqonut.singlethread.SingleThreadSolver -o out/benchmark/jmh_16g.txt
        //org.openjdk.jmh.Main.main(args);
    }

    @Setup
    public void setup() {
        singleThreadSolver = new SingleThreadSolver();
    }

    @Benchmark
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 0, jvmArgsAppend = {"-Xlog:gc*:out/gc_single.log:time,level,tags", "-Xms4g", "-Xmx4g", "-XX:+UseStringDeduplication"})
    public void process() throws IOException {
        // "-Xms8g", "-Xmx8g"
        MyFileWriter.printToFile(Constants.PERF_S, "------------------------------");

        MyFileWriter.printToFile(Constants.PERF_S, "Start time " + new Date());
        final Map<String, Double> maxTemp = new TreeMap<>();
        final Map<String, Double> minTemp = new HashMap<>();
        final Map<String, Double> sumTemp = new HashMap<>();
        final Map<String, Long> countMap = new HashMap<>();


        long start = System.currentTimeMillis();


        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (count % Constants.LOG_DISPLAYER == 0) {
                    logger.info("Read file {}", (count));
                }
                String[] parts = line.split(";");
                String city = parts[0].trim();
                double temperature = Double.parseDouble(parts[1].trim());
                maxTemp.put(city, Math.max(maxTemp.getOrDefault(city, Double.MIN_VALUE), temperature));
                minTemp.put(city, Math.min(minTemp.getOrDefault(city, Double.MAX_VALUE), temperature));
                countMap.put(city, countMap.getOrDefault(city, 0L) + 1);
                sumTemp.put(city, sumTemp.getOrDefault(city, 0.0) + temperature);
                count++;
            }
        }

        for (Map.Entry<String, Double> e : maxTemp.entrySet()) {
            String city = e.getKey();
            double avg = Math.round((sumTemp.get(city) / countMap.get(city) * 100.0) / 100.0);
            double min = Math.round((minTemp.get(city) * 100.0) / 100.0);
            double max = Math.round(e.getValue() * 100.0 / 100.0);

            StringBuilder sb = new StringBuilder();
            sb.append(city).append(':').append(min).append('/').append(avg).append('/').append(max).append(';');
            MyFileWriter.printToFile(Constants.CSV_OUTPUT_SINGLE, sb.toString());

        }

        long end = System.currentTimeMillis();
        long duration = end - start;
        MyFileWriter.printToFile(Constants.PERF_S, "Duration " + duration);
    }
}
