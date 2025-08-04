package espe.edu.ec.notificaciones.service;

import espe.edu.ec.notificaciones.dto.NotificacionDto;
import espe.edu.ec.notificaciones.entity.Notificacion;
import espe.edu.ec.notificaciones.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {
    @Autowired
    private NotificacionRepository notificacionRepository;



    public void guardarNotificacion(NotificacionDto dto){
        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());
        notificacion.setFecha(LocalDateTime.now());

        notificacionRepository.save(notificacion);

    }

    public List<Notificacion> listarTodas(){
        return notificacionRepository.findAll();
    }


}
