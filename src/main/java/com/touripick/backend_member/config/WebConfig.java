package com.touripick.backend_member.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 관련 설정을 담당하는 Configuration 클래스
 * CORS 설정 등을 포함합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS(Cross-Origin Resource Sharing) 설정
     * Frontend에서 Backend API로의 요청을 허용합니다.
     * 
     * @param registry CORS 레지스트리
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 허용할 Origin 설정 (개발 환경)
                .allowedOriginPatterns("*")
                // 허용할 HTTP 메서드 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 허용할 헤더 설정
                .allowedHeaders("*")
                // 인증 정보 포함 허용 (쿠키, Authorization 헤더 등)
                .allowCredentials(true)
                // Preflight 요청의 캐시 시간 설정 (초 단위)
                .maxAge(3600);
    }
} 