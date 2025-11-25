package com.example.aitemperaturebackend.Service;

import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存一个仿真 session 的相关对象
 */
public class SimulationSession {
    private final String sessionId;
    private okhttp3.WebSocket simWebSocket;
    private final Set<WebSocketSession> frontendSessions = ConcurrentHashMap.newKeySet();

    public SimulationSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() { return sessionId; }
    public okhttp3.WebSocket getSimWebSocket() { return simWebSocket; }
    public void setSimWebSocket(okhttp3.WebSocket simWebSocket) { this.simWebSocket = simWebSocket; }
    public Set<WebSocketSession> getFrontendSessions() { return frontendSessions; }

    /**
     * 发送前端控制指令到 Python 仿真微服务
     * payload 格式示例：
     * {"type":"control","variable":"TEY","mode":"increase"}
     * 或
     * {"type":"set_value","variable":"AT","value":150.0}
     */
    public boolean sendControlToSimulator(String messageJson) {
        if (simWebSocket != null) {
            simWebSocket.send(messageJson);
            return true;
        }
        return false;
    }
}

