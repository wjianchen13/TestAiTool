package com.example.testaitool;


public class CheckTimeCounter {

    private long time1 = 0;
    private String mFlag;
    private boolean isLogStart = false;

    public CheckTimeCounter(String flag) {
        mFlag = flag;
    }

    public void start() {
        time1 = System.currentTimeMillis();
        if(isLogStart) {
            log("start time: " + time1);
        }
    }

    public void end(String point) {
        long time2 = System.currentTimeMillis();
        long duration = time2 - time1;
        log(point + " end time: " + time2 + "   duration: " + duration);
    }

    public void log(String str) {
//        BaseLogUtils.d("Check_log", "==================> " + mFlag + "   " + str);
    }

}
