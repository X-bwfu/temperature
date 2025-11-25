package com.example.aitemperaturebackend.Controller;

import com.example.aitemperaturebackend.Model.DeviceStatusRequest;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/device")
public class DeviceStatusController {

    // 用于保存每个设备的最新上报状态（内存）
    private final Map<String, DeviceStatusRequest> deviceStatusMap = new ConcurrentHashMap<>();

    // 接收设备状态上报
    @PostMapping("/{deviceId}/status")
    public Map<String, Object> uploadStatus(
            @PathVariable String deviceId,
            @RequestBody DeviceStatusRequest request
    ) {
        request.setDeviceId(deviceId); // 强制以路径参数为准

        deviceStatusMap.put(deviceId, request);

        System.out.println("收到设备状态：" + request);

        return Map.of("status", "success");
    }

    // 返回在线、离线设备数量
    @GetMapping("/summary")
    public Map<String, Object> getDeviceSummary() {
        int onlineCount = 0;
        int offlineCount = 0;

        LocalDateTime now = LocalDateTime.now();

        for (DeviceStatusRequest status : deviceStatusMap.values()) {
            if (status.getTimestamp() == null) {
                offlineCount++;
                continue;
            }

            // 最后报告时间在 5 分钟内算在线
            long minutes = Duration.between(status.getTimestamp(), now).toMinutes();
            if (minutes <= 5) {
                onlineCount++;
            } else {
                offlineCount++;
            }
        }

        return Map.of(
                "onlineCount", onlineCount,
                "offlineCount", offlineCount,
                "totalCount", deviceStatusMap.size()
        );
    }
}
