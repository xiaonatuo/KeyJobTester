package com.keyware.jobtester.xxljob.bean;

import java.io.Serializable;

/**
 * @author GuoXin
 * @date 2022-06-17
 */
public class ReturnT<T> implements Serializable {
    public static final long serialVersionUID = 42L;

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;

    public static final com.xxl.job.core.biz.model.ReturnT<String> SUCCESS = new com.xxl.job.core.biz.model.ReturnT<String>(null);
    public static final com.xxl.job.core.biz.model.ReturnT<String> FAIL = new com.xxl.job.core.biz.model.ReturnT<String>(FAIL_CODE, null);

    private int code;
    private String msg;
    private T content;

    public ReturnT(){}
    public ReturnT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ReturnT(T content) {
        this.code = SUCCESS_CODE;
        this.content = content;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getContent() {
        return content;
    }
    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ReturnT [code=" + code + ", msg=" + msg + ", content=" + content + "]";
    }

}
