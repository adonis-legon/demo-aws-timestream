package com.example.demoawstimestream;

import lombok.Data;

@Data
public class ApplicationMetrics {
    private ApplicationIdentifier identifier;
    private double memoryUsed;
    private double cpuUsed;
    private double activeThreads;
    private double upTime;
}
