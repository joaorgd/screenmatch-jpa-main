package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface que estende JpaRepository, o coração do Spring Data JPA.
// Ao estender JpaRepository<Serie, Long>, o Spring automaticamente nos fornece
// uma implementação completa com todos os métodos CRUD (Create, Read, Update, Delete)
// para a entidade 'Serie', cuja chave primária é do tipo 'Long'.
// Não é necessário escrever nenhuma implementação para métodos como save(), findAll(), findById(), etc.
public interface SerieRepository extends JpaRepository<Serie, Long> {
}