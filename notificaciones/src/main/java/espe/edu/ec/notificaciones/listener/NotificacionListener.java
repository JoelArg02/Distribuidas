package espe.edu.ec.notificaciones.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import espe.edu.ec.notificaciones.dto.NotificacionDto;
import espe.edu.ec.notificaciones.service.NotificacionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificacionListener {

    @Autowired
    private NotificacionService service;

    @Autowired
    private ObjectMapper mapper;

    @RabbitListener(queues = "notificaciones.cola")
    public void recibirMensajes(String mensajeJson) {
        try{
            NotificacionDto dto = mapper.readValue(mensajeJson, NotificacionDto.class);
            service.guardarNotificacion(dto);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
