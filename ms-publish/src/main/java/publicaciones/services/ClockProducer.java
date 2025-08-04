package publicaciones.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import publicaciones.dto.ClientTimeDto;

import java.time.Instant;

@Service
public class ClockProducer {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String nodeName = "ms-publish";

    public void sendTime() {
        try {
            ClientTimeDto clientTimeDto = new ClientTimeDto(nodeName, Instant.now().toEpochMilli());
            amqpTemplate.convertAndSend("reloj.solicitd", objectMapper.writeValueAsString(clientTimeDto));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
