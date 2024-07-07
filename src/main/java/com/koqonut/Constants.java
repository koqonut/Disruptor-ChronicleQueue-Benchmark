package com.koqonut;

public class Constants {

    public static final int RECORDS_TO_READ_1M = 1_000_000;
    public static final int RECORDS_TO_READ_10M = 10_000_000;
    public static final int RECORDS_TO_READ_100M = 100_000_000;
    public static final int RECORDS_TO_READ_1B = 1_000_000_000;

    public static String CSV_INPUT_1M = "src/main/data/measurements_1M.txt";
    public static String CSV_INPUT_10M = "src/main/data/measurements_10M.txt";
    public static String CSV_INPUT_100M = "src/main/data/measurements_100M.txt";
    public static String CSV_INPUT_1B = "src/main/data/measurements_1B.txt";

    public static final int LOG_DISPLAYER = 500_000;

    //public static final int RECORDS_TO_READ = RECORDS_TO_READ_1M;
    //public static String CSV_INPUT_FILEPATH = CSV_INPUT_1M;

    public static final int RECORDS_TO_READ = RECORDS_TO_READ_10M;
    public static String CSV_INPUT_FILEPATH = CSV_INPUT_10M;

    //public static final int RECORDS_TO_READ = RECORDS_TO_READ_100M;
    //public static String CSV_INPUT_FILEPATH = CSV_INPUT_100M;


    //public static final int RECORDS_TO_READ = RECORDS_TO_READ_1M;
    //public static String CSV_INPUT_FILEPATH = CSV_INPUT_1M;


    public static String PERF_Q = "out/perf_q.txt";
    public static String PERF_LQ = "out/perf_lq.txt";
    public static String PERF_D = "out/perf_d.txt";
    public static String PERF_S = "out/perf_s.txt";
    public static String CQ_INPUT_PATH = "out/journal/input";
    public static String CQ_BL_PATH = "out/journal/bl";
    public static String CSV_OUTPUT_DIS = "out/output_disrupt.txt";
    public static String CSV_OUTPUT_BQ = "out/output_BQ.txt";

    public static String CSV_OUTPUT_CHQ = "out/output_chq.txt";
    public static String CSV_OUTPUT_SINGLE = "out/output_single.txt";

    public static String EOF = "EOF!!!!";
}
