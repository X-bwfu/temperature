package com.example.aitemperaturebackend.Service;

import com.example.aitemperaturebackend.Model.AlarmStatus;
import com.example.aitemperaturebackend.Model.AlarmSummary;
import com.example.aitemperaturebackend.Model.SensorData;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RealtimeService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // 前端连接 SSE
    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    // 这里要改成 List<SensorData>
    public void broadcast(List<SensorData> batch) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("telemetry")
                        .data(batch));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }

}
