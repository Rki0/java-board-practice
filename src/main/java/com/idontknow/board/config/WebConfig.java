package com.idontknow.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // resourcePath로 접근하면 Spring이 savePath에서 찾아주게 하는 설정!

    private String resourcePath = "/upload/**"; // view 에서 접근할 경로
//    private String savePath = "file:///C:/springboot_img/"; // 실제 파일 저장 경로(Window)
    private String savePath = "file:///Users/사용자이름/springboot_img/"; // 실제 파일 저장 경로(Mac)

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourcePath).addResourceLocations(savePath);
    }
}
