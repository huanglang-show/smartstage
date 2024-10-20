package com.hl.config;

import com.hl.filter.RequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 * 
 * 
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
//  @Bean
//  public RequestFilter requestFilter() {
//      return new RequestFilter();
//  }
//
//  @Bean
//  public FilterRegistrationBean filterRegister(@Autowired RequestFilter requestFilter) {
//      try {
//          // 注册过滤器
//          FilterRegistrationBean registration = new FilterRegistrationBean(requestFilter);
//          registration.addUrlPatterns("/api/*");
//
//          // 添加安全相关的初始化参数
//          registration.addInitParameter("securityEnabled", "true");
//          registration.addInitParameter("xssProtection", "true");
//          registration.addInitParameter("contentSecurityPolicy", "default-src 'self'");
//
//          return registration;
//      } catch (Exception e) {
//          // 记录异常日志
//          log.error("Failed to register filter", e);
//          throw new RuntimeException("Failed to register filter", e);
//      }
//  }

}
