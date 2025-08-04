package publicaciones.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import publicaciones.dto.AdjustmentDto;

@Service
public class ClockAdjustmentListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "reloj.ajustment")
    public void recibirAjuste(String messageJson) {
        try {
            AdjustmentDto dto = objectMapper.readValue(messageJson, AdjustmentDto.class);
            long nuevoTiempo = dto.getAjusteMillis();

            System.out.println("Ajustando reloj local a: " + nuevoTiempo + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
