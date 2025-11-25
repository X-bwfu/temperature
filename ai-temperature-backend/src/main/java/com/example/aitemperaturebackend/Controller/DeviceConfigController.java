package com.example.aitemperaturebackend.Controller;

import com.example.aitemperaturebackend.Model.DeviceConfig;
import com.example.aitemperaturebackend.Service.DeviceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
public class DeviceConfigController {

    @Autowired
    private DeviceConfigService service;

    // GET 获取配置
    @GetMapping("/{deviceId}/config")
    public DeviceConfig getConfig(@PathVariable String deviceId) {
        return service.getConfig(deviceId);
    }

    // POST 更新配置
    @PostMapping("/{deviceId}/config")
    public String updateConfig(@PathVariable String deviceId,
                               @RequestBody DeviceConfig config) {
        service.updateConfig(deviceId, config);
        return "ok";
    }
}
