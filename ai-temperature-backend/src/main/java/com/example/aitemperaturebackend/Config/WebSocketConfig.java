package com.example.aitemperaturebackend.Config;

import com.example.aitemperaturebackend.websocket.FrontendWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final FrontendWebSocketHandler handler;

    public WebSocketConfig(FrontendWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // {sessionId} 占位需要自己解析，下面注册 path "/lab/ws" 并把 sessionId 作为 query param （或 path 后手动解析）
        // 更直接方式：注册 /lab/ws/** 并在 handler 内解析 URI
        registry.addHandler(handler, "/lab/ws/*").setAllowedOrigins("*");
    }
}
