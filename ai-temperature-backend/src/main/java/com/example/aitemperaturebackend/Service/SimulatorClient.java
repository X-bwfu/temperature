package com.example.aitemperaturebackend.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Component
public class SimulatorClient {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${simulator.base-url}")
    private String simulatorBase;

    @Value("${simulator.ws-base}")
    private String simulatorWsBase;

    private final SessionManager sessionManager;

    public SimulatorClient(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * 调用 Python 微服务创建新的仿真会话
     * POST /api/v1/simulator/create
     */
    public JsonNode createSimulation(Map<String, Object> initialState) throws IOException {
        String url = simulatorBase + "/api/v1/simulator/create";

        String json = mapper.writeValueAsString(initialState);
        MediaType mt = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, mt);

        Request req = new Request.Builder()
                .url(url)
                .addHeader("X-User-ID", "test_admin") // 必填，根据文档可先写固定值
                .post(body)
                .build();


        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("Unexpected code " + resp);
            }
            return mapper.readTree(resp.body().string());
        }
    }

    /**
     * 连接 Python 微服务的 WebSocket
     * 用于推送预测结果、状态更新等
     */
    public void connectWebSocketForSession(String sessionId) {
        SimulationSession simSession = sessionManager.getSession(sessionId);
        if (simSession == null) return;

        String wsUrl = simulatorWsBase + "/api/v1/simulator/connect/" + sessionId;
        Request request = new Request.Builder().url(wsUrl).build();

        WebSocketListener listener = new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                simSession.setSimWebSocket(webSocket);
                System.out.println("Connected to simulator websocket for session " + sessionId);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 将仿真微服务推送的消息转发给所有前端 WebSocketSession
                for (WebSocketSession frontend : simSession.getFrontendSessions()) {
                    try {
                        if (frontend.isOpen()) {
                            frontend.sendMessage(new TextMessage(text));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
                String msg = "{\"type\":\"error\",\"detail\":\"" + t.getMessage() + "\"}";
                for (WebSocketSession frontend : simSession.getFrontendSessions()) {
                    try {
                        if (frontend.isOpen()) {
                            frontend.sendMessage(new TextMessage(msg));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        client.newWebSocket(request, listener);
    }

    /**
     * 发送控制指令到 Python 微服务
     * POST /api/v1/simulator/control/{sessionId}
     */
    public boolean sendToSimulator(String sessionId, String messageJson) {
        SimulationSession session = sessionManager.getSession(sessionId);
        if (session != null && session.getSimWebSocket() != null) {
            session.getSimWebSocket().send(messageJson);
            return true;
        }
        return false;
    }


    /**
     * 关闭仿真 WebSocket
     */
    public void closeSimulatorWebSocket(String sessionId) {
        SimulationSession session = sessionManager.getSession(sessionId);
        if (session == null) return;

        WebSocket ws = session.getSimWebSocket();
        if (ws != null) {
            ws.close(1000, "Session closed");
        }
    }
}
