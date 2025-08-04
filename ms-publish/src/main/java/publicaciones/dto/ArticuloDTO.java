package publicaciones.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloDTO {

	// Atributos comunes
	private String titulo;
	private String editorial;
	private int anioPublicacion;
	private String isbn;
	private String resumen;
	private Long autorId;

	// Atributos específicos de artículo científico
	private String revista;
	private String doi;
	private String areaInvestigacion;
	private Date fechaPublicacion;
}
