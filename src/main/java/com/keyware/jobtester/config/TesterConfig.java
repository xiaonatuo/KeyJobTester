package com.keyware.jobtester.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author GuoXin
 * @date 2022-06-18
 */
@Data
@Component
@ConfigurationProperties(prefix = "tester")
public class TesterConfig {
    /**
     * 任务组名称（执行器名称）
     */
    private String jobGroupName;
    /**
     * 任务的CRON表达式
     */
    private String jobCron;
    /**
     * 设置任务执行次数
     */
    private Integer jobRunNum;
    /**
     * 任务执行最大延时（毫秒）
     */
    private Long jobRunMaxTimeDiff;

    /**
     * 任务的运行模式，当前仅支持BEAN模式
     */
    private String jobGlueType = "BEAN";
}
