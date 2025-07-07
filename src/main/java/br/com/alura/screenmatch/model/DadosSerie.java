package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// @JsonIgnoreProperties(ignoreUnknown = true) é crucial. Garante que, se a API
// retornar campos que não mapeamos (ex: "Director", "Writer"), a aplicação não quebre.
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(
        // @JsonAlias permite que o nome do atributo em Java ("titulo") seja diferente
        // do nome do campo no JSON ("Title"). Isso é ótimo para seguir as convenções do Java.
        @JsonAlias("Title") String titulo,
        @JsonAlias("totalSeasons") Integer totalTemporadas,
        @JsonAlias("imdbRating") String avaliacao,

        // A classe foi expandida para capturar mais informações da API,
        // tornando o modelo de dados da aplicação muito mais completo e útil.
        @JsonAlias("Genre") String genero,
        @JsonAlias("Actors") String atores,
        @JsonAlias("Poster") String poster,
        @JsonAlias("Plot") String sinopse
) {

    // Sobrescrever o método toString() nos permite controlar exatamente como
    // as informações do objeto serão exibidas, facilitando a depuração e a visualização no console.
    @Override
    public String toString() {
        return "Série: " + titulo + "\n" +
                "  Total de Temporadas: " + totalTemporadas + "\n" +
                "  Avaliação IMDb: " + avaliacao + "\n" +
                "  Gênero: " + genero + "\n" +
                "  Atores: " + atores + "\n" +
                "  Pôster: " + poster + "\n" +
                "  Sinopse: " + sinopse + "\n";
    }
}