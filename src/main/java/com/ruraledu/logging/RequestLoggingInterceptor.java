package com.ruraledu.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Interceptor for logging HTTP requests with correlation IDs for traceability.
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request, @org.springframework.lang.NonNull HttpServletResponse response, @org.springframework.lang.NonNull Object handler) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        request.setAttribute("correlationId", correlationId);
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        logger.info("[{}] {} {} - IP: {}", 
            correlationId,
            request.getMethod(),
            request.getRequestURI(),
            getClientIp(request));
        
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        return true;
    }

    @Override
    public void afterCompletion(@org.springframework.lang.NonNull HttpServletRequest request, @org.springframework.lang.NonNull HttpServletResponse response, 
                                @org.springframework.lang.NonNull Object handler, @org.springframework.lang.Nullable Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        String correlationId = (String) request.getAttribute("correlationId");
        long duration = System.currentTimeMillis() - (startTime != null ? startTime : System.currentTimeMillis());
        
        if (ex != null) {
            logger.error("[{}] {} {} - Status: {} - Duration: {}ms - Error: {}", 
                correlationId,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration,
                ex.getMessage(),
                ex);
        } else {
            logger.info("[{}] {} {} - Status: {} - Duration: {}ms", 
                correlationId,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
