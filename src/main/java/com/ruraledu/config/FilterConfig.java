package com.ruraledu.config;

import com.ruraledu.filter.LowBandwidthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LowBandwidthFilter> loggingFilter(){
        FilterRegistrationBean<LowBandwidthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LowBandwidthFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
