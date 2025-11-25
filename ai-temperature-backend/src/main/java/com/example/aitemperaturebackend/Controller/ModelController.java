package com.example.aitemperaturebackend.Controller;

import com.example.aitemperaturebackend.Model.ModelRegistry;
import com.example.aitemperaturebackend.Service.ModelManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weights")
public class ModelController {

    @Autowired
    private ModelManagerService modelManagerService;

    /** ① 获取模型列表 */
    @GetMapping
    public List<ModelRegistry> getWeights(@RequestHeader("X-User-ID") String userId) {
        return modelManagerService.getUserWeights(userId);
    }

    /** ② 挂载模型到内存 */
    @PostMapping("/{modelId}/mount")
    public Map<String, Object> mountModel(@PathVariable String modelId) {
        modelManagerService.mountModel(modelId);
        return Map.of("status", "mounted");
    }

    /** ③ 卸载模型 */
    @PostMapping("/{modelId}/unmount")
    public Map<String, Object> unmountModel(@PathVariable String modelId) {
        modelManagerService.unmountModel(modelId);
        return Map.of("status", "unmounted");
    }

    /** ④ 启动在线训练 */
    @PostMapping("/{modelId}/train/start")
    public Map<String, Object> startTraining(@PathVariable String modelId) {
        modelManagerService.startTraining(modelId);
        return Map.of("status", "training_started");
    }

    /** ⑤ 暂停训练 */
    @PostMapping("/{modelId}/train/pause")
    public Map<String, Object> pauseTraining(@PathVariable String modelId) {
        modelManagerService.pauseTraining(modelId);
        return Map.of("status", "training_paused");
    }

    /** ⑥ 导出模型权重 */
    @PostMapping("/{modelId}/export")
    public ModelRegistry exportWeights(
            @PathVariable String modelId,
            @RequestHeader("X-User-ID") String userId,
            @RequestParam String newName
    ) {
        return modelManagerService.exportWeights(userId, modelId, newName);
    }

}

