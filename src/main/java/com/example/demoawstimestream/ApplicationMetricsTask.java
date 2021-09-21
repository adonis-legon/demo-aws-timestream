package com.example.demoawstimestream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationMetricsTask {

    @Autowired
    private ApplicationMetricsService applicationMetricsService;
    
    @Scheduled(cron = "*/10 * * ? * *")
    public void reportMetrics(){
        ApplicationMetrics applicationMetrics = null;
        try {
            applicationMetrics = applicationMetricsService.getCurrentMetrics();
        } catch (Exception e) {
            log.error("Error collecting application metrics. " + e.getMessage());
        }

        if(applicationMetrics != null){
            try {
                applicationMetricsService.publishMetrics(applicationMetrics);
                log.info(applicationMetrics.toString());
            } catch (Exception e) {
                log.error("Error publishing application metrics. " + e.getMessage());
            }
        }
    }
}
