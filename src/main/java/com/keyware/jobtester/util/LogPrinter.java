package com.keyware.jobtester.util;

/**
 * @author GuoXin
 * @date 2022-06-18
 */
public class LogPrinter {
    public static void print(String message) {
        System.out.println("【"+ message +"】");
    }
    public static void print(String title, String message) {
        System.out.println("【"+ title +"】 " + message);
    }
}
