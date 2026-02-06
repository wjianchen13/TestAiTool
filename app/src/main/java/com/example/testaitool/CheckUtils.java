package com.example.testaitool;

public class CheckUtils {

    private static long startTime = 0;

    public static void start() {
        startTime = System.currentTimeMillis();
        log("start time: " + startTime);
    }

    public static void end() {
        long endTime = System.currentTimeMillis();
        long duration = endTime -startTime;
        log("end time: " + endTime + "   duration: " + duration);
    }

    public static void log(String str) {
//        BaseLogUtils.d("Block_log", "==================> " + str);
    }

}
