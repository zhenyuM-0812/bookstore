package com.example.order.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor jwtForwardingInterceptor() {

        return requestTemplate -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes)
                            RequestContextHolder
                                    .getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest currentRequest =
                    attributes.getRequest();

            String authorizationHeader =
                    currentRequest.getHeader(
                            HttpHeaders.AUTHORIZATION
                    );

            if (authorizationHeader != null
                    && !authorizationHeader.isBlank()) {

                requestTemplate.header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                );
            }
        };
    }
}