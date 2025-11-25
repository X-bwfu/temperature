package com.example.aitemperaturebackend.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "sensor_id")
    private String sensorId;

    @Column(name = "sensor_name")
    private String sensorName;

    private LocalDateTime timestamp; // 你也可以改成 LocalDateTime

    private Double temperature;

    private Double humidity;

    private Double pressure;

    @Column(name = "predicted_temperature")
    private Double predictedTemperature;

    @Column(name = "alarm_triggered")
    private Boolean alarmTriggered;

    @Column(name = "alarm_message")
    private String alarmMessage;

    @Column(name = "is_uploaded")
    private Boolean isUploaded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getPredictedTemperature() {
        return predictedTemperature;
    }

    public void setPredictedTemperature(Double predictedTemperature) {
        this.predictedTemperature = predictedTemperature;
    }

    public Boolean getAlarmTriggered() {
        return alarmTriggered;
    }

    public void setAlarmTriggered(Boolean alarmTriggered) {
        this.alarmTriggered = alarmTriggered;
    }

    public String getAlarmMessage() {
        return alarmMessage;
    }

    public void setAlarmMessage(String alarmMessage) {
        this.alarmMessage = alarmMessage;
    }

    public Boolean getUploaded() {
        return isUploaded;
    }

    public void setUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getStorageLevel() {
        return storageLevel;
    }

    public void setStorageLevel(String storageLevel) {
        this.storageLevel = storageLevel;
    }

    @Column(name = "storage_level")
    private String storageLevel;


}
