package com.example.aitemperaturebackend.Repository;

import com.example.aitemperaturebackend.Entity.DeviceConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceConfigRepository extends JpaRepository<DeviceConfigEntity, String> {
}
