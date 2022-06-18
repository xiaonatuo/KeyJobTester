package com.keyware.jobtester;

import com.keyware.jobtester.core.TestMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author Administrator
 */
@SpringBootApplication
public class KeyJobTesterApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(KeyJobTesterApplication.class, args);
        TestMain testMain = context.getBean(TestMain.class);
        testMain.start();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
