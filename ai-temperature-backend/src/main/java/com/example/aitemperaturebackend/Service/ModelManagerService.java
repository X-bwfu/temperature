package com.example.aitemperaturebackend.Service;

import com.example.aitemperaturebackend.Model.ModelRegistry;
import com.example.aitemperaturebackend.Repository.ModelRegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ModelManagerService {

    private final Map<String, PredictorInstance> activePredictors = new ConcurrentHashMap<>();

    @Autowired
    private ModelRegistryRepository modelRegistryRepository;

    /** 挂载模型到内存 */
    public void mountModel(String modelId) {
        ModelRegistry model = modelRegistryRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("模型不存在"));

        // 直接用你的 PredictorInstance 构造函数
        PredictorInstance instance = new PredictorInstance(model);

        activePredictors.put(modelId, instance);
    }

    /** 卸载模型 */
    public void unmountModel(String modelId) {
        activePredictors.remove(modelId);
    }

    /** 获取用户可用模型列表（包括官方模型） */
    public List<ModelRegistry> getUserWeights(String userId) {
        return modelRegistryRepository.findAllByOwnerIdOrType(userId, "OFFICIAL");
    }

    /** 启动训练 */
    public void startTraining(String modelId) {
        PredictorInstance instance = activePredictors.get(modelId);
        if (instance != null) {
            instance.startTrainingLoop();
        }
    }

    /** 暂停训练 */
    public void pauseTraining(String modelId) {
        PredictorInstance instance = activePredictors.get(modelId);
        if (instance != null) {
            instance.pauseTrainingLoop();
        }
    }

    /** 保存权重到文件并更新数据库 */
    public ModelRegistry exportWeights(String userId, String modelId, String newModelName) {
        PredictorInstance instance = activePredictors.get(modelId);
        if (instance == null) {
            throw new RuntimeException("模型未挂载");
        }

        // 保存权重（你的 saveWeights 还只是 TODO）
        String filePath = "weights/users/" + userId + "/" + newModelName + ".pth";
        instance.saveWeights(filePath);

        // 写入数据库
        ModelRegistry newModel = new ModelRegistry();
        newModel.setId(java.util.UUID.randomUUID().toString());
        newModel.setName(newModelName);
        newModel.setType("USER");
        newModel.setOwnerId(userId);
        newModel.setArchitecture(instance.getArchitecture());
        newModel.setFilePath(filePath);
        newModel.setCreatedAt(java.time.LocalDateTime.now());

        return modelRegistryRepository.save(newModel);
    }

    /** 是否挂载 */
    public boolean isModelMounted(String modelId) {
        return activePredictors.containsKey(modelId);
    }

}

