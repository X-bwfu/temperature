package com.example.aitemperaturebackend.Controller;

import com.example.aitemperaturebackend.Service.SessionManager;
import com.example.aitemperaturebackend.Service.SimulatorClient;
import com.example.aitemperaturebackend.Service.SimulationSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/lab")
public class LabController {
    private final SimulatorClient simulatorClient;
    private final SessionManager sessionManager;
    private final ObjectMapper mapper = new ObjectMapper();

    public LabController(SimulatorClient simulatorClient, SessionManager sessionManager) {
        this.simulatorClient = simulatorClient;
        this.sessionManager = sessionManager;
    }

    /**
     * 启动实验：向仿真服务创建 session 并建立 websocket 连接
     * 请求体为 initial state（5个浮点值）
     */
    @PostMapping("/start")
    public ResponseEntity<?> startLab(@RequestBody Map<String, Object> initialState) {
        try {
            JsonNode resp = simulatorClient.createSimulation(initialState);
            String sessionId = resp.get("session_id").asText();
            SimulationSession s = sessionManager.createSession(sessionId);
            simulatorClient.connectWebSocketForSession(sessionId);
            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * 结束实验：关闭 websocket 并移除 session
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> stopLab(@PathVariable String sessionId) {
        // 关闭和仿真服务的连接
        simulatorClient.closeSimulatorWebSocket(sessionId);
        // 这里可以通知所有前端连接关闭（SessionManager 保留 frontend sessions）
        SimulationSession s = sessionManager.getSession(sessionId);
        if (s != null) {
            for (org.springframework.web.socket.WebSocketSession frontend : s.getFrontendSessions()) {
                try {
                    if (frontend.isOpen()) frontend.close();
                } catch (Exception e) { /* ignore */ }
            }
        }
        sessionManager.removeSession(sessionId);
        return ResponseEntity.ok(Map.of("stopped", sessionId));
    }
}
