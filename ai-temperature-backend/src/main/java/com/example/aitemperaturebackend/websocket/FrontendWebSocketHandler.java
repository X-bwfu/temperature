package com.example.aitemperaturebackend.websocket;

import com.example.aitemperaturebackend.Model.ModelRegistry;
import com.example.aitemperaturebackend.Service.ModelManagerService;
import com.example.aitemperaturebackend.Service.SessionManager;
import com.example.aitemperaturebackend.Service.SimulationSession;
import com.example.aitemperaturebackend.Service.SimulatorClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;

import java.net.URI;
import java.util.Map;

@Component
public class FrontendWebSocketHandler implements WebSocketHandler {

    private final SessionManager sessionManager;
    private final SimulatorClient simulatorClient;
    private final ModelManagerService modelManagerService;


    public FrontendWebSocketHandler(SessionManager sessionManager,
                                    SimulatorClient simulatorClient,
                                    ModelManagerService modelManagerService) {
        this.sessionManager = sessionManager;
        this.simulatorClient = simulatorClient;
        this.modelManagerService = modelManagerService;
    }


    private String extractSessionId(WebSocketSession session) {
        // 我们在 register 时使用 /lab/ws/*，从 URI 中解析最后一段作为 sessionId
        URI uri = session.getUri();
        if (uri == null) return null;
        String path = uri.getPath();
        if (!StringUtils.hasText(path)) return null;
        String[] parts = path.split("/");
        if (parts.length == 0) return null;
        return parts[parts.length - 1]; // 最后一个 segment
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = extractSessionId(session);
        if (sessionId == null || sessionManager.getSession(sessionId) == null) {
            // 如果没有找到对应的仿真 session，就拒绝连接
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Unknown sessionId"));
            return;
        }
        SimulationSession sim = sessionManager.getSession(sessionId);
        sim.getFrontendSessions().add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String sessionId = extractSessionId(session);
        if (sessionId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("no sessionId"));
            return;
        }

        String payload = message.getPayload().toString();
        com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(payload);
        String type = json.getString("type");

        // 先处理模型管理消息
        switch (type) {
            case "mount_model":
                String modelId = json.getString("model_id");
                try {
                    modelManagerService.mountModel(modelId);
                    session.sendMessage(new TextMessage("{\"type\":\"mounted\",\"model_id\":\"" + modelId + "\"}"));
                } catch (Exception e) {
                    session.sendMessage(new TextMessage("{\"type\":\"error\",\"detail\":\"" + e.getMessage() + "\"}"));
                }
                return;

            case "unmount_model":
                modelId = json.getString("model_id");
                modelManagerService.unmountModel(modelId);
                session.sendMessage(new TextMessage("{\"type\":\"unmounted\",\"model_id\":\"" + modelId + "\"}"));
                return;

            case "control_training":
                modelId = json.getString("model_id");
                boolean start = json.getBoolean("start");
                if (start) modelManagerService.startTraining(modelId);
                else modelManagerService.pauseTraining(modelId);
                session.sendMessage(new TextMessage("{\"type\":\"training_controlled\",\"model_id\":\"" + modelId + "\"}"));
                return;

            case "export_weights":
                modelId = json.getString("model_id");
                String userId = json.getString("user_id");
                String newName = json.getString("new_name");
                ModelRegistry newModel = modelManagerService.exportWeights(userId, modelId, newName);
                session.sendMessage(new TextMessage("{\"type\":\"weights_exported\",\"model_id\":\"" + newModel.getId() + "\"}"));
                return;
        }

        // 其他消息（control / set_value）转发给 Python 微服务 WebSocket
        SimulationSession simSession = sessionManager.getSession(sessionId);
        if (simSession != null) {
            boolean ok = simSession.sendControlToSimulator(payload); // ✅ 调用 SimulationSession 方法
            if (!ok) {
                session.sendMessage(new TextMessage("{\"type\":\"error\",\"detail\":\"Simulator websocket not connected\"}"));
            }
        } else {
            session.sendMessage(new TextMessage("{\"type\":\"error\",\"detail\":\"Unknown sessionId\"}"));
        }
    }




    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // log
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String sessionId = extractSessionId(session);
        if (sessionId == null) return;
        SimulationSession sim = sessionManager.getSession(sessionId);
        if (sim != null) {
            sim.getFrontendSessions().remove(session);
            // 如果没有前端连接并且想要立即关闭仿真连接，可以在这里判断并关闭
            if (sim.getFrontendSessions().isEmpty()) {
                // 选择性：在最后一个前端断开时销毁 session
                // 这里不自动移除 session，交由前端/控制器调用 DELETE /lab/{sessionId}
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
