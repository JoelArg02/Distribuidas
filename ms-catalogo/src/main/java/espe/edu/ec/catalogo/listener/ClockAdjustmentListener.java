package espe.edu.ec.catalogo.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import espe.edu.ec.notificaciones.dto.AdjustmentDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ClockAdjustmentListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "reloj.ajustment")
    public void recibirAjuste(String messageJson) {
        try {
            AdjustmentDto dto = objectMapper.readValue(messageJson, AdjustmentDto.class);
            long nuevoTiempo = dto.getAjusteMillis();

            System.out.println("Ajustando reloj local a: " + nuevoTiempo + " ms");
            // Aquí iría la lógica real para ajustar el tiempo local
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
