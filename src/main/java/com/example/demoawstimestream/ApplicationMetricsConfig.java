package com.example.demoawstimestream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationMetricsConfig {
    private String countryCode;
    private String customersCode;
    private String subsidiariesCode;
    private String devicesCode;
    private String timestreamDb;
    private String timestreamTable;
}
