package espe.edu.ec.notificaciones.controller;

import espe.edu.ec.notificaciones.entity.Notificacion;
import espe.edu.ec.notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    @Autowired
    private NotificacionService service;

    @GetMapping
    public List<Notificacion> listarTodas() {
        return service.listarTodas();
    }
}
