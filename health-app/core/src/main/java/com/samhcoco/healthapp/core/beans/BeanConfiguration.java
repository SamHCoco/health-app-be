package com.samhcoco.healthapp.core.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
