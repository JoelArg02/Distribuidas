package publicaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibroDTO {

	// Atributos comunes
	private String titulo;
	private String editorial;
	private int anioPublicacion;
	private String isbn;
	private String resumen;
	private Long autorId;

	// Atributos específicos del libro
	private String genero;
	private int numPaginas;
}
