package com.example.aitemperaturebackend.Repository;

import com.example.aitemperaturebackend.Model.ModelRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRegistryRepository extends JpaRepository<ModelRegistry, String> {

    // 查询当前用户的模型 + 官方模型
    List<ModelRegistry> findAllByOwnerIdOrType(String ownerId, String type);
}
