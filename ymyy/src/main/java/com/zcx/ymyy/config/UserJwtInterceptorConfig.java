package com.zcx.ymyy.config;

import com.zcx.ymyy.interceptor.UserJwtAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 接种者相关路径拦截
 */
@Configuration
public class UserJwtInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(userJwtAuthInterceptor());
        registration.addPathPatterns("/user/**");
        registration.excludePathPatterns("/user/login", "/user/save");
    }

    @Bean
    public UserJwtAuthInterceptor userJwtAuthInterceptor() {
        return new UserJwtAuthInterceptor();
    }
}
