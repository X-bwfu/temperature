package com.example.aitemperaturebackend.Service;

import com.example.aitemperaturebackend.Model.SensorData;
import com.example.aitemperaturebackend.Repository.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageService {

    @Autowired
    private SensorDataRepository repository;

    // 保存单条数据（用于 /api/sensor-data）
    public Long saveSingle(SensorData data) {
        data.setId(null);  // 保证插入新记录
        SensorData saved = repository.save(data);
        return saved.getId();
    }


    // 异步批量存储（用于 /api/sensor-data/batch）
    @Async
    public void saveBatchAsync(List<SensorData> batch) {
        batch.forEach(data -> {
            data.setId(null);  // ★ 每条都强制插入新记录
            repository.save(data);
        });
    }


    // 查询每个设备最新的一条数据
    public List<SensorData> findLatestPerDevice()
    {
        // 这里假设你使用 JPA Repository，可以写自定义方法
        return repository.findLatestPerDevice();
    }

    public List<SensorData> findLatestNByDevice(String deviceId, int n) {
        return repository.findLatestNByDevice(deviceId, n);
    }

}
