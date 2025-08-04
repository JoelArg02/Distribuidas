package publicaciones.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import publicaciones.dto.AutorDTO;
import publicaciones.dto.ResponseDto;
import publicaciones.entity.Autor;
import publicaciones.repository.AutorRepository;

@Service
public class AutorService {
	@Autowired
	private AutorRepository autorRepository;
	
	//@Autowired
	//private NotificacionProducer notificacionProducer;
	
	public ResponseDto crearAutor(AutorDTO autorDto) {
		Autor autor = new Autor();
		autor.setNombre(autorDto.getNombre());
		autor.setApellido(autorDto.getApellido());
		autor.setEmail(autorDto.getEmail());
		autor.setInstitucion(autorDto.getInstitucion());
		autor.setNacionalidad(autorDto.getNacionalidad());
		autor.setOrcid(autorDto.getOrcid());
		autor.setTelefono(autorDto.getTelefono());
		
		Autor guardado = autorRepository.save(autor);
		
		//notificacionProducer.enviarNotificacion("Nuevo autor registrado" + autor.getNombre() + autor.getApellido(), "nuevo-autor");
        return new ResponseDto("Autor registrado exitosamente", guardado);
	}
	
	public List<ResponseDto> listarAutores(){
		return autorRepository.findAll().stream()
				.map(autor ->new ResponseDto("Autor" + autor.getApellido(), autor))
				.collect(Collectors.toList());
	}
	
	public ResponseDto autorPorId(Long id){
        Autor autor = autorRepository.findById(id)
        		.orElseThrow(()-> new RuntimeException("no existe un autor con el id: " + id ));
        return new ResponseDto("Autor con id:" + autor.getId(), autor);

    }
	
	public ResponseDto eliminarAutor(Long id) {
		Autor autor = autorRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("no existe un autor con el id: " + id ));
		
		autorRepository.delete(autor);
		
		return new ResponseDto("Autor eliminado exitosamente", null);
	}
	
	public ResponseDto actualizarAutor(Long id, AutorDTO autorDto) {
		Autor autor = autorRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("no existe un autor con el id: " + id));
		
		autor.setNombre(autorDto.getNombre());
		autor.setApellido(autorDto.getApellido());
		autor.setEmail(autorDto.getEmail());
		autor.setInstitucion(autorDto.getInstitucion());
		autor.setNacionalidad(autorDto.getNacionalidad());
		autor.setOrcid(autorDto.getOrcid());
		autor.setTelefono(autorDto.getTelefono());
		
		Autor actualizado = autorRepository.save(autor);
		return new ResponseDto("Autor actualizado exitosamente ", actualizado);
	}
}