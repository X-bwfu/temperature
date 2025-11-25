package com.example.aitemperaturebackend.Model;

public class AlarmStatus {
    private String deviceId;       // 设备ID
    private boolean alarmTriggered; // 设备是否触发报警

    public AlarmStatus(String deviceId, boolean alarmTriggered) {
        this.deviceId = deviceId;
        this.alarmTriggered = alarmTriggered;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isAlarmTriggered() {
        return alarmTriggered;
    }

    public void setAlarmTriggered(boolean alarmTriggered) {
        this.alarmTriggered = alarmTriggered;
    }
// getter & setter

}
