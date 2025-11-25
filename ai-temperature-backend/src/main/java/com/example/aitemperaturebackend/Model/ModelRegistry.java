package com.example.aitemperaturebackend.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ModelRegistry {

    @Id
    private String id;

    private String name;

    private String type; // OFFICIAL / USER

    private String architecture;

    private String filePath;

    private String ownerId;

    private LocalDateTime createdAt;

    // 可选：getter & setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getArchitecture() { return architecture; }
    public void setArchitecture(String architecture) { this.architecture = architecture; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
