package com.example.aitemperaturebackend.Model;

import java.util.List;

public class DeviceConfig {

    private String deviceId;
    private String lastUpdated;

    private PredictionModel predictionModel;
    private AlarmThresholds alarmThresholds;
    private UploadSchedule uploadSchedule;

    // ⭐ 新增字段：用于每个传感器的独立配置
    private List<SensorConfig> sensorConfigs;

    // ========== Getter / Setter ==========

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

    public PredictionModel getPredictionModel() {
        return predictionModel;
    }
    public void setPredictionModel(PredictionModel predictionModel) {
        this.predictionModel = predictionModel;
    }

    public AlarmThresholds getAlarmThresholds() {
        return alarmThresholds;
    }
    public void setAlarmThresholds(AlarmThresholds alarmThresholds) {
        this.alarmThresholds = alarmThresholds;
    }

    public UploadSchedule getUploadSchedule() {
        return uploadSchedule;
    }
    public void setUploadSchedule(UploadSchedule uploadSchedule) {
        this.uploadSchedule = uploadSchedule;
    }

    public List<SensorConfig> getSensorConfigs() {
        return sensorConfigs;
    }
    public void setSensorConfigs(List<SensorConfig> sensorConfigs) {
        this.sensorConfigs = sensorConfigs;
    }


    // ========== 内部类（结构严格对应接口文档 JSON） ==========

    // ---- 1. predictionModel ----
    public static class PredictionModel {
        private String version;
        private String url;
        private String checksum;

        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }

        public String getChecksum() {
            return checksum;
        }
        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }
    }

    // ---- 2. alarmThresholds ----
    public static class AlarmThresholds {
        private double upper;
        private double lower;
        private double deviation;

        public double getUpper() {
            return upper;
        }
        public void setUpper(double upper) {
            this.upper = upper;
        }

        public double getLower() {
            return lower;
        }
        public void setLower(double lower) {
            this.lower = lower;
        }

        public double getDeviation() {
            return deviation;
        }
        public void setDeviation(double deviation) {
            this.deviation = deviation;
        }
    }

    // ---- 3. uploadSchedule ----
    public static class UploadSchedule {
        private int batchSize;
        private long intervalMs;

        public int getBatchSize() {
            return batchSize;
        }
        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public long getIntervalMs() {
            return intervalMs;
        }
        public void setIntervalMs(long intervalMs) {
            this.intervalMs = intervalMs;
        }
    }

    // ---- 4. sensorConfigs（新增：数组项结构） ----
    public static class SensorConfig {
        private String sensorId;
        private AlarmThresholds alarmThresholds;
        private long pollIntervalMs;

        public String getSensorId() {
            return sensorId;
        }
        public void setSensorId(String sensorId) {
            this.sensorId = sensorId;
        }

        public AlarmThresholds getAlarmThresholds() {
            return alarmThresholds;
        }
        public void setAlarmThresholds(AlarmThresholds alarmThresholds) {
            this.alarmThresholds = alarmThresholds;
        }

        public long getPollIntervalMs() {
            return pollIntervalMs;
        }
        public void setPollIntervalMs(long pollIntervalMs) {
            this.pollIntervalMs = pollIntervalMs;
        }
    }
}
