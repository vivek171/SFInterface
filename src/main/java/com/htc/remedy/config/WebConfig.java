package com.htc.remedy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Created by poovar/staticasanv on 12/9/17.
 * Project : remedy-web-services
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebConfig extends WebMvcConfigurationSupport {


    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry
                .addResourceHandler("/static/file/**")
                .addResourceLocations("classpath:/static/fileattachments/");

        registry
                .addResourceHandler("/static/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry
                .addResourceHandler("/static/img/**")
                .addResourceLocations("classpath:/static/img/");

        registry
                .addResourceHandler("/static/fonts/**")
                .addResourceLocations("classpath:/static/fonts/");
        registry
                .addResourceHandler("/static/images/**")
                .addResourceLocations("classpath:/static/images/");

        registry
                .addResourceHandler("/static/assets/**")
                .addResourceLocations("classpath:/static/assets/");


    }
}
