package com.jxx.approval.common.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
public class ApiLog implements HandlerInterceptor {

    private String requestId;
    private String uri;
    private String queryString;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        requestId = UUID.randomUUID().toString().substring(0, 7);
        uri = request.getRequestURI();
        queryString = request.getQueryString();

        if (queryString == null) {
            log.info("STT [{}][{}]", requestId, uri);
            return true;
        }
        else {
            log.info("STT [{}][{}?{}]", requestId, uri, queryString);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (queryString == null) {
            log.info("END [{}][{}]", requestId, uri);
        }
        else {
            log.info("END [{}][{}?{}]", requestId, uri, queryString);
        }
    }
}
