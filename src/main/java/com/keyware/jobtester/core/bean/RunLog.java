package com.keyware.jobtester.core.bean;

import com.keyware.jobtester.util.LogPrinter;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GuoXin
 * @date 2022-06-18
 */
public class RunLog {
    private static AtomicInteger index = new AtomicInteger(0);
    private static Vector<Log> logList = new Vector<>();

    public static void setLogList(Collection<Log> logs) {
        logList.clear();
        logList.addAll(logs);
    }

    public static Log get(int index) {
        return logList.get(index);
    }

    public static void runNext(String text) {
        if (index.get() < logList.size()) {
            get(index.get()).setRunTime(System.currentTimeMillis());
            get(index.get()).setResultText(text);
            LogPrinter.print("执行任务", text);
            index.incrementAndGet();
        }
    }

    public static void check(long timeDiff) {
        for (int i = index.get(); i < logList.size(); i++) {
            checkLog(get(i), timeDiff);
        }
    }

    private static void checkLog(Log log, long timeDiff) {
        long targetTime = log.getTargetTime() + timeDiff;
        long currentTime = System.currentTimeMillis();
        if (log.getRunTime() == null && targetTime < currentTime) {
            log.setResult(false);
            log.setResultText("执行失败");
            LogPrinter.print("任务执行失败", "任务没有在预期时间内执行");
            index.incrementAndGet();
        }
    }

    public static boolean isFinish() {
        return index.get() == logList.size();
    }


    public static void analyze() {
        int count = logList.size();
        int success = (int) logList.stream().filter(log -> log.getRunTime() != null).count();
        int fail = count - success;
        long maxTimeDiff = logList.stream().filter(log -> log.getRunTimeDiff() != null).mapToLong(Log::getRunTimeDiff).max().orElse(-1);
        long minTimeDiff = logList.stream().filter(log -> log.getRunTimeDiff() != null).mapToLong(Log::getRunTimeDiff).min().orElse(-1);
        long avg = (long) logList.stream().filter(log -> log.getRunTimeDiff() != null).mapToLong(Log::getRunTimeDiff).average().orElse(-1);
        LogPrinter.print("任务总数", count + "");
        LogPrinter.print("成功数", success + "");
        LogPrinter.print("失败数", fail + "");
        LogPrinter.print("成功率", success * 100 / count + "%");
        LogPrinter.print("最大执行延时", maxTimeDiff < 0 ? "-" : maxTimeDiff + " 毫秒");
        LogPrinter.print("最小执行延时", minTimeDiff < 0 ? "-" : minTimeDiff + " 毫秒");
        LogPrinter.print("平均执行延时", avg < 0 ? "-" : avg + " 毫秒");

    }

    public static void addLog(Log log) {
        logList.add(log);
    }

    @Data
    public static class Log {
        /**
         * 执行时间
         */
        private Long runTime;

        /**
         * 预期时间
         */
        private Long targetTime;

        /**
         * 执行结果
         */
        private Boolean result;

        /**
         * 执行结果
         */
        private String resultText;
        /**
         * 执行时间差
         */
        private Long runTimeDiff;

        public Log(Long targetTime) {
            this.targetTime = targetTime;
        }

        public void setRunTime(Long runTime) {
            this.result = true;
            this.runTime = runTime;
            this.runTimeDiff = runTime - targetTime;
        }

        public String getRunTimeStr() {
            return parseDate(runTime);
        }

        public String getTargetTimeStr() {
            return parseDate(targetTime);
        }

        private String parseDate(Long time) {
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format(date);
        }
    }
}
