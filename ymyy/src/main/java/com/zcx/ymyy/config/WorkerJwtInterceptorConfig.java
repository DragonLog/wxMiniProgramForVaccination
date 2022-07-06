package com.zcx.ymyy.config;

import com.zcx.ymyy.interceptor.WorkerJwtAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 医护人员相关路径拦截
 */
@Configuration
public class WorkerJwtInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(workerJwtAuthInterceptor());
        registration.addPathPatterns("/worker/**");
        registration.excludePathPatterns("/worker/login");
    }
    @Bean
    public WorkerJwtAuthInterceptor workerJwtAuthInterceptor() {
        return new WorkerJwtAuthInterceptor();
    }
}
