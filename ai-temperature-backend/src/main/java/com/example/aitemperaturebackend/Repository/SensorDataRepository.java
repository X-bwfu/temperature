package com.example.aitemperaturebackend.Repository;

import com.example.aitemperaturebackend.Model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    // 查询每个设备最新的一条记录
    @Query(value = "SELECT * FROM sensor_data s1 " +
            "WHERE s1.id = (" +
            "  SELECT id FROM sensor_data s2 " +
            "  WHERE s2.device_id = s1.device_id " +
            "  ORDER BY timestamp DESC LIMIT 1" +
            ")", nativeQuery = true)
    List<SensorData>
    findLatestPerDevice()
    ;
    // 新增：查询指定设备最近 N 条记录
    @Query(value = "SELECT * FROM sensor_data " +
            "WHERE device_id = :deviceId " +
            "ORDER BY timestamp DESC " +
            "LIMIT :n", nativeQuery = true)
    List<SensorData> findLatestNByDevice(@Param("deviceId") String deviceId, @Param("n") int n);
}
