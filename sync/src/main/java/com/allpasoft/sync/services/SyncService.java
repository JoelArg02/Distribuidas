package com.allpasoft.sync.services;

import com.allpasoft.sync.dto.AdjustmentDto;
import com.allpasoft.sync.dto.ClientTimeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SyncService {
    private final Map<String, Long> clientTimes = new ConcurrentHashMap<>();
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int INTERVAL_SECONDS = 10;

    public void registerClientTime(ClientTimeDto dto) {
        clientTimes.put(dto.getNodeName(), dto.getServerTime());
    }

    public void synchronizeClocks() {
        if (clientTimes.size() >= 2) {
            long currentTime = Instant.now().toEpochMilli();
            long averageTime = (currentTime + clientTimes.values().stream().mapToLong(Long::longValue).sum())
                    / (clientTimes.size() + 1);
            clientTimes.clear();
            applyAdjustment(averageTime);
        }
    }

    public void applyAdjustment(long averageTime) {

        try {
            AdjustmentDto adjustmentDto = new AdjustmentDto(averageTime);
            amqpTemplate.convertAndSend("reloj.ajustment", objectMapper.writeValueAsString(adjustmentDto));
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Adjusted time (ms): " + averageTime);
    }
}
