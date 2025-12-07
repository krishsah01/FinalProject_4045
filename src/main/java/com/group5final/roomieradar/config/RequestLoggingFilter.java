package com.group5final.roomieradar.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        
        String user = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";
        
        log.info("REQUEST: method={} uri={} user={} ip={}", 
                request.getMethod(), 
                request.getRequestURI(), 
                user, 
                ipAddress);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("RESPONSE: status={} duration={}ms", 
                    response.getStatus(), 
                    duration);
        }
    }
}
