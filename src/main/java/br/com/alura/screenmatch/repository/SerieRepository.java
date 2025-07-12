package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    // Busca séries que contenham o trecho de título informado, ignorando maiúsculas/minúsculas.
    List<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    // Busca séries por ator e com avaliação maior ou igual à informada.
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();
}