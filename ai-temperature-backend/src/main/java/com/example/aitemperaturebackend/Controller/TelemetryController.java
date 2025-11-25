package com.example.aitemperaturebackend.Controller;

import com.example.aitemperaturebackend.Model.SensorData;
import com.example.aitemperaturebackend.Service.RealtimeService;
import com.example.aitemperaturebackend.Service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TelemetryController {

    @Autowired
    private RealtimeService realtimeService;

    @Autowired
    private StorageService storageService;

    /**
     * A. 边缘设备上传单条数据（高优先级报警）
     * POST /api/sensor-data
     */
    @PostMapping("/sensor-data")
    public Map<String, Object> uploadSingle(@RequestBody SensorData data) {

        // 推给前端
        realtimeService.broadcast(List.of(data));

        // 存数据库
        Long recordId = storageService.saveSingle(data);

        return Map.of(
                "status", "success",
                "message", "Data received",
                "recordId", recordId
        );
    }

    /**
     * B. 批量上传数据
     * POST /api/sensor-data/batch
     */
    @PostMapping("/sensor-data/batch")
    public Map<String, Object> uploadBatch(@RequestBody List<SensorData> batch) {

        // 推给前端
        realtimeService.broadcast(batch);

        // 异步批量插入数据库
        storageService.saveBatchAsync(batch);

        return Map.of(
                "status", "success",
                "receivedCount", batch.size(),
                "processedCount", batch.size()
        );
    }

    /**
     * SSE 实时流
     */
    @GetMapping("/stream")
    public SseEmitter stream() {
        return realtimeService.connect();
    }

    // --- A. 统计设备预警状态 ---
    @GetMapping("/sensor-status-summary")
    public Map<String, Object> getSensorStatusSummary()
    {
        List<SensorData> latestData = storageService.findLatestPerDevice();

        long alarmCount =
                latestData.stream()
                        .filter(d -> Boolean.TRUE.equals(d.getAlarmTriggered()))
                        .count();
        long normalCount =
                latestData.size() - alarmCount;

        return
                Map.of(
                        "alarmCount"
                        , alarmCount,
                        "normalCount"
                        , normalCount
                );
    }

    // --- B. 返回每个设备最新一条数据 ---
    @GetMapping("/sensor-latest")
    public List<SensorData> getLatestSensorData()
    {
        return
                storageService.findLatestPerDevice();
    }

    @GetMapping("/sensor-curve/{deviceId}")
    public Map<String, Object> getSensorCurve(@PathVariable String deviceId,
                                              @RequestParam(required = false, defaultValue = "50") int limit) {
        // 从数据库获取最近 limit 条记录
        List<SensorData> records = storageService.findLatestNByDevice(deviceId, limit);

        // 时间反转成从旧到新
        Collections.reverse(records);

        List<Double> actualTemps = records.stream()
                .map(SensorData::getTemperature)
                .collect(Collectors.toList());

        List<Double> predictedTemps = records.stream()
                .map(SensorData::getPredictedTemperature)
                .collect(Collectors.toList());

        List<String> timestamps = records.stream()
                .map(r -> r.getTimestamp().toString())
                .collect(Collectors.toList());

        return Map.of(
                "timestamps", timestamps,
                "actual", actualTemps,
                "predicted", predictedTemps
        );
    }



}

