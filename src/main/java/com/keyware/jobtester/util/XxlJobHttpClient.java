package com.keyware.jobtester.util;

import com.keyware.jobtester.xxljob.bean.ReturnT;
import com.keyware.jobtester.xxljob.config.XxlJobConfig;
import com.xxl.job.core.util.GsonTool;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GuoXin
 * @date 2022-06-18
 */
@Component
public class XxlJobHttpClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private List<String> cookies = new ArrayList<>();

    public XxlJobHttpClient(XxlJobConfig jobConfig, RestTemplate restTemplate) {
        this.baseUrl = jobConfig.getAdminAddresses();
        this.restTemplate = restTemplate;
    }

    /**
     * 发送请求
     *
     * @param url  请求路径
     * @param params 请求数据
     * @return 请求结果
     */
    public ReturnT<Object> sendPost(String url, Object params) {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);

        MultiValueMap<String, Object> data = objectToMultiValueMap(params);
        return sendRequest(url, HttpMethod.POST, data, header, Object.class);
    }

    public <T> ReturnT<T> sendPost(String url, Object params, Class<T> clazz) {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
        MultiValueMap<String, Object> data = objectToMultiValueMap(params);
        return sendRequest(url, HttpMethod.POST, data, header, clazz);
    }

    public ReturnT<Object> sendGet(String url, Object data) {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return sendRequest(url, HttpMethod.GET, data, header, Object.class);
    }

    public <T> T sendGet(String url, Object data, Class<T> clazz) {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return sendRequest(url, HttpMethod.GET, data, header, clazz, null);
    }

    public <T> ReturnT<T> sendRequest(@NonNull String url, HttpMethod method, Object data, HttpHeaders headers, Class<T> responseSubType) {
        ReturnT<T> result = sendRequest(url, method, data, headers, ReturnT.class, responseSubType);
        if (result.getCode() == ReturnT.FAIL_CODE) {
            throw new RuntimeException("接口调用失败，返回消息：" + result.getMsg());
        }
        return result;
    }

    public <T, E> T sendRequest(@NonNull String url, HttpMethod method, Object params, HttpHeaders headers, Class<T> responseType, Class<E> responseSubType) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        headers.put(HttpHeaders.COOKIE, cookies);

        HttpEntity<Object> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(createUrl(url), method, entity, String.class);
        List<String> resCookies = response.getHeaders().get("Set-Cookie");
        if (resCookies != null) {
            cookies.addAll(resCookies);
        }
        String body = response.getBody();
        if(responseSubType != null) {
            T result = GsonTool.fromJson(body, responseType, responseSubType);
            Assert.notNull(result, "接口调用异常，返回结果解析错误:" + body);
            return result;
        }else{
            T result = GsonTool.fromJson(body, responseType);
            Assert.notNull(result, "接口调用异常，返回结果解析错误:" + body);
            return result;
        }
    }

    /**
     * 创建请求URL
     *
     * @param uri 请求路径
     * @return 完整请求URL字符串
     */
    private String createUrl(String uri) {
        String url = baseUrl;
        if (baseUrl.endsWith("/")) {
            url = baseUrl.substring(0, baseUrl.length() - 2);
        }
        if (!uri.startsWith("/")) {
            uri += "/";
        }
        return url + uri;
    }

    private MultiValueMap<String, Object> objectToMultiValueMap(Object obj) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (obj == null) {
            return map;
        }
        if (obj instanceof Map) {
            Map<String, Object> mapObj = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : mapObj.entrySet()) {
                map.add(entry.getKey(), entry.getValue());
            }
        } else {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    map.add(field.getName(), field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}
