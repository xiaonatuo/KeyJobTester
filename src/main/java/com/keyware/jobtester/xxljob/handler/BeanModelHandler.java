package com.keyware.jobtester.xxljob.handler;

import com.keyware.jobtester.core.bean.RunLog;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @author GuoXin
 * @date 2022-06-13
 */
@Component
public class BeanModelHandler {

    @XxlJob("bean-job-handler-1")
    public void beanJobHandler() {
        RunLog.runNext("bean-job-handler-1 执行成功");
    }



}
