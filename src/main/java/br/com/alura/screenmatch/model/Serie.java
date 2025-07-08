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
        try {
            this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        } catch (NumberFormatException e) {
            this.avaliacao = 0.0; // Segurança extra caso o valor seja inválido
        }


        // Converte a string de gênero da API para o tipo seguro Categoria.
        // O código inteligentemente pega apenas o primeiro gênero caso a API retorne vários (ex: "Action, Crime").
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());

        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = dadosSerie.sinopse();
    }

    // Getters e Setters permitem que outras partes do código acessem e modifiquem
    // os atributos privados da classe de forma controlada.
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return
                "Gênero=" + genero +
                        ", Título='" + titulo + '\'' +
                        ", Total de Temporadas=" + totalTemporadas +
                        ", Avaliação=" + avaliacao +
                        ", Atores='" + atores + '\'' +
                        ", Pôster='" + poster + '\'' +
                        ", Sinopse='" + sinopse + '\'';
    }
}