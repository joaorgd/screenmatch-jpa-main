package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Importe a List
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    // Altere o retorno de Optional<Serie> para List<Serie> para lidar com múltiplos resultados.
    List<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
}