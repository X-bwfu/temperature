package com.example.aitemperaturebackend.Model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeviceStatusRequest {
    private String deviceId;
    private LocalDateTime timestamp;

    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskFreeGB;
    private String networkStatus;
    private String appVersion;

    private List<ActiveSensor> activeSensors;

    @Data
    public static class ActiveSensor {
        private String sensorId;
        private String status;
        private LocalDateTime lastReadTime;
        private String lastError;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Double getDiskFreeGB() {
        return diskFreeGB;
    }

    public void setDiskFreeGB(Double diskFreeGB) {
        this.diskFreeGB = diskFreeGB;
    }

    public String getNetworkStatus() {
        return networkStatus;
    }

    public void setNetworkStatus(String networkStatus) {
        this.networkStatus = networkStatus;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public List<ActiveSensor> getActiveSensors() {
        return activeSensors;
    }

    public void setActiveSensors(List<ActiveSensor> activeSensors) {
        this.activeSensors = activeSensors;
    }
}
