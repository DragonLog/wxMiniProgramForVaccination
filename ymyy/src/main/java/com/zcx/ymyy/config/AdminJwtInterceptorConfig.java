package com.zcx.ymyy.config;

import com.zcx.ymyy.interceptor.AdminJwtAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 管理员相关路径拦截
 */
@Configuration
public class AdminJwtInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration registration = registry.addInterceptor(adminJwtAuthInterceptor());
        registration.addPathPatterns("/admin/**");
        registration.excludePathPatterns("/admin/login");
    }

    @Bean
    public AdminJwtAuthInterceptor adminJwtAuthInterceptor() {
        return new AdminJwtAuthInterceptor();
    }
}
