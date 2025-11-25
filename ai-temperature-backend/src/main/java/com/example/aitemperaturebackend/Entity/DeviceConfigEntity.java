package com.example.aitemperaturebackend.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "device_config")
public class DeviceConfigEntity {

    @Id
    private String deviceId;

    private String lastUpdated;

    // predictionModel
    private String modelVersion;
    private String modelUrl;
    private String modelChecksum;

    // alarmThresholds
    private Double alarmUpper;
    private Double alarmLower;
    private Double alarmDeviation;

    // uploadSchedule
    private Integer batchSize;
    private Long intervalMs;

    // sensorConfigs - 存 JSON
    @Column(columnDefinition = "JSON")
    private String sensorConfigsJson;

    // ===== getter / setter =====
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getModelVersion() {
        return modelVersion;
    }
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getModelUrl() {
        return modelUrl;
    }
    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public String getModelChecksum() {
        return modelChecksum;
    }
    public void setModelChecksum(String modelChecksum) {
        this.modelChecksum = modelChecksum;
    }

    public Double getAlarmUpper() {
        return alarmUpper;
    }
    public void setAlarmUpper(Double alarmUpper) {
        this.alarmUpper = alarmUpper;
    }

    public Double getAlarmLower() {
        return alarmLower;
    }
    public void setAlarmLower(Double alarmLower) {
        this.alarmLower = alarmLower;
    }

    public Double getAlarmDeviation() {
        return alarmDeviation;
    }
    public void setAlarmDeviation(Double alarmDeviation) {
        this.alarmDeviation = alarmDeviation;
    }

    public Integer getBatchSize() {
        return batchSize;
    }
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Long getIntervalMs() {
        return intervalMs;
    }
    public void setIntervalMs(Long intervalMs) {
        this.intervalMs = intervalMs;
    }

    public String getSensorConfigsJson() {
        return sensorConfigsJson;
    }
    public void setSensorConfigsJson(String sensorConfigsJson) {
        this.sensorConfigsJson = sensorConfigsJson;
    }
}
