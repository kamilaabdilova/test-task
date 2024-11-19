package org.example.testtaskmega.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        // Логирование запроса
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        logger.info("Incoming Request: method={} uri={} remoteAddr={}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpRequest.getRemoteAddr());

        // Продолжаем выполнение
        try {
            chain.doFilter(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        // Логирование ответа
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        logger.info("Outgoing Response: status={}", httpResponse.getStatus());
    }
}
