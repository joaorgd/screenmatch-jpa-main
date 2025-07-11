package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SerieRepository extends JpaRepository<Serie, Long> {
    // Adicione este método:
    // O nome do método é uma instrução para o Spring Data JPA:
    // "Crie uma busca que encontre uma Serie (Optional<Serie>) pelo Titulo (ByTitulo)
    // que contenha o texto que eu passar (Containing) e ignore maiúsculas/minúsculas (IgnoreCase)."
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
}