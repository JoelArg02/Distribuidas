package publicaciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import publicaciones.dto.AutorDTO;
import publicaciones.dto.ResponseDto;
import publicaciones.services.AutorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/autores")
public class AutorController {
	@Autowired
	private AutorService autorService;
	
	//crear autor
	@PostMapping
	public ResponseDto crearAutor(@RequestBody AutorDTO autorDto) {
		return autorService.crearAutor(autorDto);
	}
	
	//listar autores
	@GetMapping
	public List<ResponseDto> obtenerAutores() {
		return autorService.listarAutores();
	}
	//obttener autor por id
	@GetMapping("/{id}")
	public ResponseDto busacarPorId(@PathVariable Long id) {
		return autorService.autorPorId(id);
	}
	
	//actualizar autor
	@PutMapping("actualizar/{id}")
	public ResponseDto actualziarAutor(@PathVariable Long id, @RequestBody AutorDTO autorDto) {
		return autorService.actualizarAutor(id, autorDto);
	}
	
	//eliminar autor
	@DeleteMapping("eliminar/{id}")
	public ResponseDto eliminarAutor(@PathVariable Long id) {
		return autorService.eliminarAutor(id);
	}
}
