package com.doppler.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app")
public class AppConfig {
    public Doppler doppler = new Doppler();
    public Redis redis = new Redis();
    String applicationName;
    String username;
    String password;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Bean
    @ConfigurationProperties("doppler")
    public Doppler getDoppler() {
        return doppler;
    }

    @Bean
    @ConfigurationProperties("redis")
    public Redis getRedis() {
        return redis;
    }


    @Override
    public String toString() {
        return "AppConfig {\n" +
                "  applicationName: '" + applicationName + '\'' +
                ",\n  username: '" + username + '\'' +
                ",\n  password: '" + password + '\'' +
                ",\n  doppler: " + doppler +
                ",\n  redis: " + redis +
                "\n}\n";
    }
}
