package com.samhcoco.healthapp.core.configuration;

import com.google.gson.Gson;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Autowired
    private JavaMailConfiguration mailConfiguration;

    @Bean
    public JavaMailSender getJavaMailSender() {
        val emailSender = new JavaMailSenderImpl();
        emailSender.setHost(mailConfiguration.getHost());
        emailSender.setPort(mailConfiguration.getPort());
        emailSender.setUsername(mailConfiguration.getUsername());
        emailSender.setPassword(mailConfiguration.getPassword());

        val properties = emailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", mailConfiguration.getSmptAuth());
        properties.put("mail.smtp.starttls.enable", "true");

        return emailSender;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Gson getGson() {
        return new Gson();
    }

}
