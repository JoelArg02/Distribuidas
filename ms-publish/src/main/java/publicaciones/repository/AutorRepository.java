package publicaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import publicaciones.entity.Autor;

public interface AutorRepository extends JpaRepository<Autor, Long>{
	
}
