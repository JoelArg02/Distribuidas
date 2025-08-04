package publicaciones.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import publicaciones.entity.Articulo;

import publicaciones.dto.ArticuloDTO;
import publicaciones.dto.LibroDTO;
import publicaciones.dto.ResponseDto;
import publicaciones.entity.Articulo;
import publicaciones.entity.Autor;
import publicaciones.repository.ArticuloRepository;
import publicaciones.repository.AutorRepository;

@Service
public class ArticuloService {
	@Autowired
	private ArticuloRepository articuloRepository;
	
	@Autowired
	private AutorRepository autorRepository;
	
	@Autowired
	private CatalogoProducer catalogoProducer;
	
	//crear libro
	public ResponseDto crearArticulo(ArticuloDTO articuloDTO) {
		Autor autor = autorRepository.findById(articuloDTO.getAutorId())
				.orElseThrow(()-> new RuntimeException("no existe autor con este id" + articuloDTO.getAutorId())); 
		Articulo articulo = new Articulo();
		articulo.setAutor(autor);
		articulo.setAnioPublicacion(articuloDTO.getAnioPublicacion());
		articulo.setAreaInvestigacion(articuloDTO.getAreaInvestigacion());
		articulo.setDoi(articuloDTO.getDoi());
		articulo.setEditorial(articuloDTO.getEditorial());
		articulo.setFechaPublicacion(articuloDTO.getFechaPublicacion());
		articulo.setIsbn(articuloDTO.getIsbn());
		articulo.setResumen(articuloDTO.getResumen());
		articulo.setRevista(articuloDTO.getRevista());
		articulo.setTitulo(articuloDTO.getTitulo());
		catalogoProducer.enviarCatalogo(articulo.getTitulo(), articulo.getAutor().getNombre() + " " + articulo.getAutor().getApellido(), articulo.getResumen(), "nuevo articulo");
		Articulo nuevo = articuloRepository.save(articulo);
		
		
		return new ResponseDto("articulo registrado", nuevo);
	}
	public List<Articulo> getAllArticles() {
		return articuloRepository.findAll();
	}
	
	
	
}
