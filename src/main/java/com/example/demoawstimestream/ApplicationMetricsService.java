package com.example.demoawstimestream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.Sample;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.Dimension;
import software.amazon.awssdk.services.timestreamwrite.model.MeasureValueType;
import software.amazon.awssdk.services.timestreamwrite.model.Record;
import software.amazon.awssdk.services.timestreamwrite.model.WriteRecordsRequest;

@Service
public class ApplicationMetricsService {
    private Map<String, String> metricNameToPropertyMap;

    private TimestreamWriteClient timestreamWriteClient;

    private Random random;

    public ApplicationMetricsService() {
        metricNameToPropertyMap = new HashMap<>();

        metricNameToPropertyMap.put("jvm.memory.used", "MemoryUsed");
        metricNameToPropertyMap.put("process.cpu.usage", "CpuUsed");
        metricNameToPropertyMap.put("jvm.threads.live", "ActiveThreads");
        metricNameToPropertyMap.put("process.uptime", "UpTime");

        random = new Random();

        timestreamWriteClient = TimestreamWriteClient.builder().build();
    }

    @Autowired
    private MetricsEndpoint metricsEndpoint;

    @Autowired
    private ApplicationMetricsConfig applicationMetricsConfig;

    public ApplicationMetrics getCurrentMetrics() throws ApplicationMetricsException {
        ApplicationMetrics applicationMetrics = new ApplicationMetrics();

        String[] customersCode = applicationMetricsConfig.getCustomersCode().split(",");
        String[] subsidiariesCode = applicationMetricsConfig.getSubsidiariesCode().split(",");
        String[] devicesCode = applicationMetricsConfig.getDevicesCode().split(",");

        applicationMetrics.setIdentifier(new ApplicationIdentifier(applicationMetricsConfig.getCountryCode(),
                customersCode[random.nextInt(customersCode.length)],
                subsidiariesCode[random.nextInt(subsidiariesCode.length)],
                devicesCode[random.nextInt(devicesCode.length)]));

        for (String metricName : metricNameToPropertyMap.keySet()) {
            List<Sample> measurements = metricsEndpoint.metric(metricName, null).getMeasurements();
            if (measurements != null && !measurements.isEmpty()) {
                try {
                    applicationMetrics.getClass()
                            .getMethod("set" + metricNameToPropertyMap.get(metricName), new Class[] { double.class })
                            .invoke(applicationMetrics, Precision.round(measurements.get(0).getValue(), 3));
                } catch (Exception e) {
                    throw new ApplicationMetricsException(e);
                }
            }
        }

        return applicationMetrics;
    }

    public void publishMetrics(ApplicationMetrics applicationMetrics) throws ApplicationMetricsException {
        List<Record> records = new ArrayList<>();
        final long time = System.currentTimeMillis();
        List<Dimension> dimensions = new ArrayList<>();

        dimensions.add(Dimension.builder().name("country_code")
                .value(applicationMetrics.getIdentifier().getCountryCode()).build());
        dimensions.add(Dimension.builder().name("customer_code")
                .value(applicationMetrics.getIdentifier().getCustomerCode()).build());
        dimensions.add(Dimension.builder().name("subsidiary_code")
                .value(applicationMetrics.getIdentifier().getSubsidiaryCode()).build());
        dimensions.add(Dimension.builder().name("device_code").value(applicationMetrics.getIdentifier().getDeviceCode())
                .build());

        for (String metricName : metricNameToPropertyMap.keySet()) {
            try {
                Record measureRecord = Record.builder().dimensions(dimensions).measureValueType(MeasureValueType.DOUBLE)
                        .measureName(metricName.replace(".", "_"))
                        .measureValue(
                                applicationMetrics.getClass().getMethod("get" + metricNameToPropertyMap.get(metricName))
                                        .invoke(applicationMetrics).toString())
                        .time(String.valueOf(time)).build();

                records.add(measureRecord);
            } catch (Exception e) {
                throw new ApplicationMetricsException(e);
            }
        }

        WriteRecordsRequest writeRecordsRequest = WriteRecordsRequest.builder()
                .databaseName(applicationMetricsConfig.getTimestreamDb())
                .tableName(applicationMetricsConfig.getTimestreamTable()).records(records).build();

        try {
            timestreamWriteClient.writeRecords(writeRecordsRequest);
        } catch (Exception e) {
            throw new ApplicationMetricsException(e);
        }

    }
}
