package com.keyware.jobtester.xxljob.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author GuoXin
 * @date 2022-06-13
 */
@Data
@Component
@ConfigurationProperties(prefix = "xxl-job")
public class XxlJobConfig {
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);
    private String adminAddresses;
    private String accessToken;
    private String executorAppname;
    private String executorAddress;
    private String executorIp;
    private int executorPort;
    private String executorLogPath;
    private int executorLogRetentionDays;


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(executorAppname);
        xxlJobSpringExecutor.setAddress(executorAddress);
        xxlJobSpringExecutor.setIp(executorIp);
        xxlJobSpringExecutor.setPort(executorPort);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(executorLogPath);
        xxlJobSpringExecutor.setLogRetentionDays(executorLogRetentionDays);

        return xxlJobSpringExecutor;
    }
}