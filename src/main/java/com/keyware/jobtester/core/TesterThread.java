package com.keyware.jobtester.core;

import com.keyware.jobtester.config.TesterConfig;
import com.keyware.jobtester.core.bean.CronExpression;
import com.keyware.jobtester.core.bean.RunLog;
import com.keyware.jobtester.core.service.XxlJobAdminService;
import com.keyware.jobtester.util.LogPrinter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Date;

/**
 * @author GuoXin
 * @date 2022-06-18
 */
@Slf4j
@Component
public class TesterThread implements Runnable {
    /**
     * xxl-job 执行任务准备时间
     */
    private static final long PRE_READ_MS = 5000;

    private final XxlJobAdminService service;
    private final TesterConfig testerConfig;

    public TesterThread(XxlJobAdminService service, TesterConfig testerConfig) {
        this.service = service;
        this.testerConfig = testerConfig;
    }

    @Override
    public void run() {
        try {
            // 初始化任务组
            service.createJobGroup();

            // 创建任务
            service.createJob();

            // 解析并预生成执行日志
            parseRunLogs();

            // 执行任务
            if (service.startJob()) {
                LogPrinter.print("任务启动成功");

                // 等待任务执行完毕
                while (!RunLog.isFinish()) {
                    RunLog.check(testerConfig.getJobRunMaxTimeDiff());
                    Thread.sleep(100);
                }
                LogPrinter.print("任务执行完毕");
                // 解析执行日志
                RunLog.analyze();
            } else {
                LogPrinter.print("任务启动失败");
            }
        } catch (Exception e) {
            log.error("【测试失败】", e);
        } finally {
            service.stopJob();
            service.removeJob();
        }

        System.out.println("【测试完成】");
        //System.exit(0);
    }

    /**
     * 获取cron表达式的执行时间
     *
     * @return 时间戳（毫秒）
     */
    private void parseRunLogs() {
        LogPrinter.print("初始化执行日志");
        String cron = testerConfig.getJobCron();
        Assert.isTrue(CronExpression.isValidExpression(testerConfig.getJobCron()), "cron表达式不合法");
        Date lastTime = new Date(System.currentTimeMillis() + PRE_READ_MS);
        for (int i = 0; i < testerConfig.getJobRunNum(); i++) {
            try {
                lastTime = new CronExpression(cron).getNextValidTimeAfter(lastTime);
                if (lastTime != null) {
                    RunLog.addLog(new RunLog.Log(lastTime.getTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
