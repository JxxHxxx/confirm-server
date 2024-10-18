package com.jxx.approval.common.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
public class ApiAccessLogInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();
//        String remoteHost = request.getRemoteHost();
        String remoteAddr = request.getRemoteAddr();
        String httpMethod = request.getMethod();
        int httpStatusCode = response.getStatus();
        Map<String, String[]> params = request.getParameterMap();
        StringBuilder param = createParamUri(params);
        // log format [요청을 보낸 클라이언트 IP][API HTTP 메서드][API URI][API param][API HttpStatusCode]
        log.info("[{}][{}][{}][{}][{}]", remoteAddr, httpMethod, uri, param, httpStatusCode);
    }

    private static StringBuilder createParamUri(Map<String, String[]> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : params.keySet()) {
            stringBuilder.append(param + "=");
            String[] values = params.get(param);

            int valueSize = values.length;
            for (int idx = 0; idx < valueSize; idx++) {
                stringBuilder.append(values[idx]);
                if (idx + 1 < valueSize) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(" ");
        }
        return stringBuilder;
    }
}
