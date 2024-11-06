package com.hl.config;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

public class RequestContextHolder {
    private static final ThreadLocal<HttpServletRequest> contextHolder = new ThreadLocal<>();

    public static void setContext(HttpServletRequest request) {
        contextHolder.set(request);
    }

    public static HttpServletRequest getContext() {
        return contextHolder.get();
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
