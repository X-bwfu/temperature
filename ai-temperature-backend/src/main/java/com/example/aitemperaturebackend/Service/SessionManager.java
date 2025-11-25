package com.example.aitemperaturebackend.Service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class SessionManager {
    private final Map<String, SimulationSession> sessions = new ConcurrentHashMap<>();

    public SimulationSession createSession(String sessionId) {
        SimulationSession s = new SimulationSession(sessionId);
        sessions.put(sessionId, s);
        return s;
    }

    public SimulationSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
