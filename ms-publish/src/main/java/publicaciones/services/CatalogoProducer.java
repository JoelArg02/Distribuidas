package publicaciones.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import publicaciones.dto.CatalogoDto;
import publicaciones.dto.NotificacionDto;

@Service
public class CatalogoProducer {
	@Autowired
	private RabbitTemplate template;
	
	@Autowired
	private ObjectMapper mapper;
	
	public void enviarCatalogo(String nombre, String autor, String resumen, String mensaje) {
		try {
			CatalogoDto catalogoDto = new CatalogoDto(nombre, autor, resumen, mensaje);
			String json = mapper.writeValueAsString(catalogoDto);
			template.convertAndSend("catalog.cola", json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
