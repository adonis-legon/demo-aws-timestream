package com.example.demoawstimestream;

public class ApplicationMetricsException extends Exception{
    public ApplicationMetricsException(Exception ex) {
        super("Error in application metrics. Message: " + ex.getMessage(), ex);
    }
}
