package com.example.aitemperaturebackend.Model;

import java.util.List;

public class AlarmSummary {
    private int triggeredCount;          // 当前触发预警的设备数量
    private List<AlarmStatus> devices;   // 每个设备的预警状态

    public AlarmSummary(int triggeredCount, List<AlarmStatus> devices) {
        this.triggeredCount = triggeredCount;
        this.devices = devices;
    }

    public int getTriggeredCount() {
        return triggeredCount;
    }

    public void setTriggeredCount(int triggeredCount) {
        this.triggeredCount = triggeredCount;
    }

    public List<AlarmStatus> getDevices() {
        return devices;
    }

    public void setDevices(List<AlarmStatus> devices) {
        this.devices = devices;
    }
// getter & setter
}

