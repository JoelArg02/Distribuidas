package publicaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import publicaciones.entity.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {

}
