package com.allpasoft.sync.listener;

import com.allpasoft.sync.dto.ClientTimeDto;
import com.allpasoft.sync.services.SyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class clockListener {

    @Autowired
    private SyncService syncService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "reloj.solicitd")
    public void receiveMessage(String jsonMessage) {
        try {
            ClientTimeDto dto = objectMapper.readValue(jsonMessage, ClientTimeDto.class);
            System.out.println(dto);
            syncService.registerClientTime(dto);
        } catch (Exception e) {
            System.err.println("Failed to process message: " + jsonMessage);
            e.printStackTrace();
        }
    }
}
