package com.example.aitemperaturebackend.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class TelemetryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveBatch(List<Map<String, Object>> batch) {
        String sql = "INSERT INTO temperature_data (sensor_id, record_time, temperature) VALUES (?, ?, ?)";


        for (Map<String, Object> record : batch) {
            String sensorId = (String) record.get("sensor_id");  // 改成你前端或Jetson上传的字段名
            double temperature = Double.parseDouble(record.get("temperature").toString());
            Timestamp recordTime = Timestamp.valueOf(record.get("record_time").toString().replace("T", " "));
            jdbcTemplate.update(sql, sensorId, recordTime, temperature);

        }
    }
}
