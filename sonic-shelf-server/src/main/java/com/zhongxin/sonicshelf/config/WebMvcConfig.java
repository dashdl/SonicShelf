package com.zhongxin.sonicshelf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 对所有API路径启用跨域
                .allowedOrigins("*")  // 允许所有来源，生产环境应该设置具体的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")  // 允许的请求头
                .exposedHeaders("Authorization")  // 暴露的响应头
                .allowCredentials(false)  // 是否允许携带凭证
                .maxAge(3600);  // 预检请求的有效期，单位秒
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String basePath = System.getProperty("user.dir");

        // 将/uploads/**路径映射到本地文件系统中的相应目录
        String uploadPath = basePath + "/files/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);

        String coverPath = basePath + "/files/cover/";
        registry.addResourceHandler("/cover/**")
                .addResourceLocations("file:" + coverPath);

        String songsPath = basePath + "/files/songs/";
        registry.addResourceHandler("/songs/**")
                .addResourceLocations("file:" + songsPath);
    }
}