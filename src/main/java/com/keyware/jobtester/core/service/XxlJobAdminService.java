package com.keyware.jobtester.core.service;

import com.keyware.jobtester.config.TesterConfig;
import com.keyware.jobtester.util.LogPrinter;
import com.keyware.jobtester.util.XxlJobHttpClient;
import com.keyware.jobtester.xxljob.bean.ReturnT;
import com.keyware.jobtester.xxljob.bean.XxlJobGroup;
import com.keyware.jobtester.xxljob.bean.XxlJobInfo;
import com.keyware.jobtester.xxljob.config.XxlJobConfig;
import com.xxl.job.core.util.GsonTool;
import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GuoXin
 * @date 2022-06-14
 */
@Service
public class XxlJobAdminService {
    private final XxlJobConfig jobConfig;
    private final XxlJobHttpClient httpClient;
    private final TesterConfig testerConfig;

    private XxlJobGroup jobGroup;
    private XxlJobInfo jobInfo;


    public XxlJobAdminService(XxlJobConfig xxlJobConfig, XxlJobHttpClient jobHttpClient, TesterConfig testerConfig) {
        this.testerConfig = testerConfig;
        this.jobConfig = xxlJobConfig;
        this.httpClient = jobHttpClient;
        this.loginAdmin();
    }

    /**
     * 创建任务
     */
    public void createJob() {
        LogPrinter.print("创建临时任务");
        jobInfo = new XxlJobInfo();
        jobInfo.setJobGroup(jobGroup.getId());
        jobInfo.setJobDesc("临时测试任务");
        jobInfo.setAuthor("admin");
        jobInfo.setScheduleType("CRON");
        jobInfo.setScheduleConf(testerConfig.getJobCron());
        jobInfo.setGlueType("BEAN");
        jobInfo.setExecutorHandler("bean-job-handler-1");
        jobInfo.setExecutorRouteStrategy("FIRST");
        jobInfo.setMisfireStrategy("DO_NOTHING");
        jobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");

        ReturnT<String> result = httpClient.sendPost("/jobinfo/add", jobInfo, String.class);
        if (result.getCode() == ReturnT.SUCCESS_CODE) {
            String jobId = result.getContent();
            jobInfo.setId(Integer.parseInt(jobId));
        }
    }

    /**
     * 开始任务，任务将按照任务设定执行
     */
    public boolean startJob() {
        ReturnT<Object> result = httpClient.sendGet("/jobinfo/start?id=" + jobInfo.getId(), null);
        return result.getCode() == ReturnT.SUCCESS_CODE;
    }

    public boolean stopJob() {
        ReturnT<Object> result = httpClient.sendGet("/jobinfo/stop?id=" + jobInfo.getId(), null);
        return result.getCode() == ReturnT.SUCCESS_CODE;
    }

    /**
     * 删除任务
     */
    public void removeJob() {
        httpClient.sendGet("/jobinfo/remove?id=" + jobInfo.getId(), null);
    }

    public List<String> getNextTriggerTime(){
        List<String> list = new ArrayList<>();
        return list;
    }

    /**
     * 创建任务组
     */
    public void createJobGroup() {
        LogPrinter.print("初始化任务组");
        jobGroup = getJobGroupByName(jobConfig.getExecutorAppname());
        if (jobGroup == null) {
            {
                jobGroup = new XxlJobGroup();
                jobGroup.setAppname(jobConfig.getExecutorAppname());
                jobGroup.setTitle(jobConfig.getExecutorAppname());
                jobGroup.setAddressType(0); // 自动注册地址

                httpClient.sendPost("/jobgroup/save", jobGroup);
                jobGroup = getJobGroupByName(jobConfig.getExecutorAppname());
            }
        }
        // 执行更新，使自动注册地址生效
        if (StringUtils.isEmpty(jobGroup.getAddressList())) {
            jobGroup.setUpdateTime(null);
            httpClient.sendPost("/jobgroup/update", jobGroup);
            jobGroup = getJobGroupByName(jobConfig.getExecutorAppname());
        }
    }

    /**
     * 查询任务组
     *
     * @param name 任务组名称
     * @return 任务组
     */
    public XxlJobGroup getJobGroupByName(@NonNull String name) {
        Map<String, Object> result = httpClient.sendGet("/jobgroup/pageList?length=999", null, Map.class);

        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) result.get("data");
        return list.stream().map(map -> {
            String data = GsonTool.toJson(map);
            return GsonTool.fromJson(data, XxlJobGroup.class);
        }).filter(group -> group.getAppname().equals(name)).findFirst().orElse(null);
    }


    /**
     * 获取xxl-job-admin管理员token
     *
     * @return 管理员token
     */
    public void loginAdmin() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userName", "admin");
        body.add("password", "123456");
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        httpClient.sendRequest("/login", HttpMethod.POST, body, header, String.class);
    }
}
