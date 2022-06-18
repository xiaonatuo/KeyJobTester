package com.keyware.jobtester.core;

import com.xxl.job.core.thread.ExecutorRegistryThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author GuoXin
 * @date 2022-06-14
 */
@Slf4j
@Component
public class TestMain {
    @Autowired
    private TesterThread testerThread;

    public void start() {
        boolean isOk = false;
        while (!isOk) {
            isOk = ExecutorRegistryThread.getInstance().isRunning();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new Thread(testerThread).start();
    }
}
