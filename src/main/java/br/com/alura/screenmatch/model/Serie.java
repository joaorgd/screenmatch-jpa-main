package br.com.alura.screenmatch.model;

import java.util.OptionalDouble;

public class Serie {
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    private Categoria genero; // Uso do enum para garantir a consistência do gênero.
    private String atores;
    private String poster;
    private String sinopse;

    // Construtor que transforma um objeto de dados da API (DTO) num objeto de domínio (Entidade).
    // Esta é uma prática essencial para desacoplar a lógica da aplicação dos dados externos.
    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();

        // Tratamento robusto para a avaliação, que pode vir como "N/A" da API.
        // OptionalDouble evita erros de conversão (NumberFormatException) e atribui 0 caso falhe.
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);

        // Converte a string de gênero da API para o tipo seguro Categoria.
        // O código inteligentemente pega apenas o primeiro gênero caso a API retorne vários (ex: "Action, Crime").
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());

        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = dadosSerie.sinopse();
    }
}