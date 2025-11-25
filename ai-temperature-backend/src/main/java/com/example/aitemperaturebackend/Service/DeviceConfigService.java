package com.example.aitemperaturebackend.Service;

import com.example.aitemperaturebackend.Entity.DeviceConfigEntity;
import com.example.aitemperaturebackend.Model.DeviceConfig;
import com.example.aitemperaturebackend.Repository.DeviceConfigRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DeviceConfigService {

    @Autowired
    private DeviceConfigRepository repo;

    private final ObjectMapper mapper = new ObjectMapper();

    // 查询配置
    public DeviceConfig getConfig(String deviceId) {
        DeviceConfigEntity entity =
                repo.findById(deviceId).orElseGet(() -> {
                    DeviceConfigEntity e = new DeviceConfigEntity();
                    e.setDeviceId(deviceId);
                    e.setLastUpdated(Instant.now().toString());
                    repo.save(e);
                    return e;
                });

        return convertToModel(entity);
    }

    // 更新配置
    public void updateConfig(String deviceId, DeviceConfig config) {
        DeviceConfigEntity entity =
                repo.findById(deviceId).orElse(new DeviceConfigEntity());

        entity.setDeviceId(deviceId);
        entity.setLastUpdated(Instant.now().toString());

        // 模型
        if (config.getPredictionModel() != null) {
            entity.setModelVersion(config.getPredictionModel().getVersion());
            entity.setModelUrl(config.getPredictionModel().getUrl());
            entity.setModelChecksum(config.getPredictionModel().getChecksum());
        }

        // 全局阈值
        if (config.getAlarmThresholds() != null) {
            entity.setAlarmUpper(config.getAlarmThresholds().getUpper());
            entity.setAlarmLower(config.getAlarmThresholds().getLower());
            entity.setAlarmDeviation(config.getAlarmThresholds().getDeviation());
        }

        // 上传策略
        if (config.getUploadSchedule() != null) {
            entity.setBatchSize(config.getUploadSchedule().getBatchSize());
            entity.setIntervalMs(config.getUploadSchedule().getIntervalMs());
        }

        // ⭐ 传感器配置 List → JSON
        if (config.getSensorConfigs() != null) {
            try {
                String json = mapper.writeValueAsString(config.getSensorConfigs());
                entity.setSensorConfigsJson(json);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save sensorConfigs", e);
            }
        }

        repo.save(entity);
    }

    // Entity → DTO
    private DeviceConfig convertToModel(DeviceConfigEntity e) {
        DeviceConfig cfg = new DeviceConfig();

        cfg.setDeviceId(e.getDeviceId());
        cfg.setLastUpdated(e.getLastUpdated());

        // 模型
        DeviceConfig.PredictionModel model = new DeviceConfig.PredictionModel();
        model.setVersion(e.getModelVersion());
        model.setUrl(e.getModelUrl());
        model.setChecksum(e.getModelChecksum());
        cfg.setPredictionModel(model);

        // 阈值
        DeviceConfig.AlarmThresholds thresholds = new DeviceConfig.AlarmThresholds();
        thresholds.setUpper(e.getAlarmUpper() != null ? e.getAlarmUpper() : 0);
        thresholds.setLower(e.getAlarmLower() != null ? e.getAlarmLower() : 0);
        thresholds.setDeviation(e.getAlarmDeviation() != null ? e.getAlarmDeviation() : 0);
        cfg.setAlarmThresholds(thresholds);

        // 上传策略
        DeviceConfig.UploadSchedule schedule = new DeviceConfig.UploadSchedule();
        schedule.setBatchSize(e.getBatchSize() != null ? e.getBatchSize() : 100);
        schedule.setIntervalMs(e.getIntervalMs() != null ? e.getIntervalMs() : 300000);
        cfg.setUploadSchedule(schedule);

        // ⭐ JSON → List<SensorConfig>
        if (e.getSensorConfigsJson() != null) {
            try {
                List<DeviceConfig.SensorConfig> list =
                        mapper.readValue(
                                e.getSensorConfigsJson(),
                                new TypeReference<List<DeviceConfig.SensorConfig>>() {}
                        );
                cfg.setSensorConfigs(list);
            } catch (Exception ex) {
                cfg.setSensorConfigs(List.of());
            }
        }

        return cfg;
    }
}
