package com.example.aitemperaturebackend.Service;

import com.example.aitemperaturebackend.Model.ModelRegistry;

public class PredictorInstance {

    private ModelRegistry model;       // 对应数据库里的模型信息
    private String architecture;       // 模型架构
    private boolean running;           // 在线训练状态
    private Object modelObject;        // 真正的模型对象，例如 PyTorch/TF 模型
    // 这里用 Object 先占位，后续可换成你实际用的模型类

    public PredictorInstance(ModelRegistry model) {
        this.model = model;
        this.architecture = model.getArchitecture();
        this.running = false;
        this.modelObject = null; // TODO: 这里可以实例化实际模型
    }

    public void startTrainingLoop() {
        running = true;
        // TODO: 加入训练线程/循环
    }

    public void pauseTrainingLoop() {
        running = false;
        // TODO: 暂停训练线程
    }

    public void saveWeights(String filePath) {
        // TODO: 保存模型权重到文件
    }

    // getter & setter
    public ModelRegistry getModel() { return model; }
    public boolean isRunning() { return running; }
    public String getArchitecture() { return architecture; }

    private double lastPrediction; // 保存最新一次预测值

    public double getPredictionValue() {
        return lastPrediction;
    }

    /**
     * 模型前向推理后调用
     */
    public void setPredictionValue(double prediction) {
        this.lastPrediction = prediction;
    }

}
