package publicaciones.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import publicaciones.entity.Articulo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import publicaciones.dto.ArticuloDTO;
import publicaciones.dto.ResponseDto;
import publicaciones.services.ArticuloService;

@RestController
@RequestMapping("/articulos")
public class ArticuloController {
	@Autowired
	private ArticuloService articuloService;
	
	@PostMapping
	public ResponseDto crearArticulo(@RequestBody ArticuloDTO articuloDTO) {
		return articuloService.crearArticulo(articuloDTO);
	}

	@GetMapping
	public ResponseEntity<List<Articulo>> getAllArticles() {
		return ResponseEntity.ok(articuloService.getAllArticles());
	}
}
