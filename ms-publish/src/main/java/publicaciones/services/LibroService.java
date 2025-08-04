package publicaciones.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import publicaciones.dto.LibroDTO;
import publicaciones.dto.ResponseDto;
import publicaciones.entity.Autor;
import publicaciones.entity.Libro;
import publicaciones.repository.AutorRepository;
import publicaciones.repository.LibroRepository;

@Service
public class LibroService {
	@Autowired
	private LibroRepository libroRepository;
	
	@Autowired 
	private AutorRepository autorRepository;
	
	//@Autowired
	//private NotificacionProducer notificacionProducer;
	
	@Autowired
	private CatalogoProducer catalogoProducer;
	
	//crear libro
	public ResponseDto crearLibro(LibroDTO libroDto) {
		Autor autor = autorRepository.findById(libroDto.getAutorId())
                .orElseThrow(()-> new RuntimeException("No existe autor con id: " + libroDto.getAutorId()));
		Libro libro = new Libro();
		libro.setAutor(autor);
		libro.setAnioPublicacion(libroDto.getAnioPublicacion());
		libro.setEditorial(libroDto.getEditorial());
		libro.setGenero(libroDto.getGenero());
		libro.setIsbn(libroDto.getIsbn());
		libro.setNumPaginas(libroDto.getNumPaginas());
		libro.setResumen(libroDto.getResumen());
		libro.setTitulo(libroDto.getTitulo());
		
		Libro nuevo = libroRepository.save(libro);
		catalogoProducer.enviarCatalogo(libro.getTitulo(), libro.getAutor().getNombre() + " " + libro.getAutor().getApellido(), libro.getResumen(), "nuevo-catalogo");
		//notificacionProducer.enviarNotificacion("Nuevo libro registrado" + libro.getTitulo(), "nuevo-libro");
		return new ResponseDto("Libro registrado exitosamente", nuevo);
	}
	
	//actualizar libro 
	public ResponseDto actualizarLibro(Long id, LibroDTO libroDto) {
		
		Libro libro = libroRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("no exixste un libro con el id: " + id));
		
		Autor autor = autorRepository.findById(libroDto.getAutorId())
				.orElseThrow(()-> new RuntimeException("No existe el autor con el id: " + libroDto.getAutorId()));
		libro.setAutor(autor);
		libro.setAnioPublicacion(libroDto.getAnioPublicacion());
		libro.setEditorial(libroDto.getEditorial());
		libro.setGenero(libroDto.getGenero());
		libro.setIsbn(libroDto.getGenero());
		libro.setNumPaginas(libroDto.getNumPaginas());
		libro.setResumen(libroDto.getResumen());
		libro.setTitulo(libroDto.getTitulo());
		Libro actualizado = libroRepository.save(libro);
		return new ResponseDto("libro actaualizado", actualizado);
	}
	
	//eliminar libro
	public ResponseDto eliminarLibro(Long id) {
		Libro libro = libroRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("No existe un libro con ese id: "+ id));
		libroRepository.delete(libro);
		return new ResponseDto("libro eliminado exitosamente ", null);
	}
	
	//todos los libros
	public List<ResponseDto> listarLibros() {
		return libroRepository.findAll().stream()
				.map(libro -> new ResponseDto("libro: " + libro.getTitulo(), libro))
				.collect(Collectors.toList());
	}
	
	//libro por id
	public ResponseDto libroPorId(Long id) {
		Libro libro = libroRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("no existe un libro con el id: " + id));
		return new ResponseDto("libro con id: " + libro.getId(), libro);
	}
}
