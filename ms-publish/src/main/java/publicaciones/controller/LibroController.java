package publicaciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import publicaciones.dto.AutorDTO;
import publicaciones.dto.LibroDTO;
import publicaciones.dto.ResponseDto;
import publicaciones.services.AutorService;
import publicaciones.services.LibroService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/")
public class LibroController {
	@Autowired
	private LibroService libroService;
	
	//crear libro
	@PostMapping
	public ResponseDto crearLibro(@RequestBody LibroDTO libroDto) {
		return libroService.crearLibro(libroDto);
	}
	
	//listar libros
	@GetMapping
	public List<ResponseDto> obtenerLibros(){
		return libroService.listarLibros();
	}
	
	//obtener un libro por id  
	@GetMapping("/{id}")
	public ResponseDto buscarPorId(@PathVariable Long id) {
		return libroService.libroPorId(id);
	}
	
	//actualizar libro
	@PutMapping("actualizar/{id}")
	public  ResponseDto actualizarLibro(@PathVariable Long id, @RequestBody LibroDTO libroDto) {
		return libroService.actualizarLibro(id, libroDto);
	}
	
	//eliminar autor
	@DeleteMapping("eliminar/{id}")
	public ResponseDto eliminarLibro(@PathVariable Long id) {
		return libroService.eliminarLibro(id);
	}
}
