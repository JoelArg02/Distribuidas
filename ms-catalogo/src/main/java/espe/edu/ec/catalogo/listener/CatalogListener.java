package espe.edu.ec.catalogo.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import espe.edu.ec.catalogo.dto.CatalogDto;
import espe.edu.ec.catalogo.service.CatalogService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatalogListener {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "catalog.cola")
    public void recibirMensaje(String mensaje) {
        try {
            CatalogDto dto = objectMapper.readValue(mensaje, CatalogDto.class);
            catalogService.saveEntity(dto);
            System.out.println("Mensaje procesado y guardado: " + dto);
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
        }
    }
}
