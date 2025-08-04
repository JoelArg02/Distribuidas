package publicaciones.dto;

public class CatalogoDto {
	private String nombre;
	private String autor;
	private String resumen;
	private String mensaje;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getResumen() {
		return resumen;
	}
	public void setResumen(String resumen) {
		this.resumen = resumen;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public CatalogoDto(String nombre, String autor, String resumen, String mensaje) {
		this.nombre = nombre;
		this.autor = autor;
		this.resumen = resumen;
		this.mensaje = mensaje;
	}
	
	
	
}
