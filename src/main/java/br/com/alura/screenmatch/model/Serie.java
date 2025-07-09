package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.traducao.ConsultaMyMemory;
import jakarta.persistence.*;
import java.util.OptionalDouble;

// @Entity: Marca esta classe como uma entidade JPA, ou seja, um objeto que pode ser persistido no banco de dados.
@Entity
// @Table: Especifica o nome da tabela no banco de dados que esta entidade irá representar.
@Table(name = "series")
public class Serie {
    // @Id: Designa este campo como a chave primária da tabela.
    @Id
    // @GeneratedValue: Configura a estratégia de geração da chave primária.
    // GenerationType.IDENTITY indica que o próprio banco de dados será responsável por gerar e auto-incrementar o valor.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(unique = true): Adiciona uma restrição ao banco de dados para garantir
    // que não possam existir duas séries com o mesmo título.
    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    // @Enumerated(EnumType.STRING): Instrui o JPA a salvar o enum 'Categoria' como uma String no banco
    // (ex: "ACAO", "COMEDIA"), o que é muito mais legível que o padrão (que salva números).
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    // Construtor padrão (sem argumentos) é uma exigência do JPA para criar instâncias da entidade.
    public Serie() {}

    // Construtor para criar a entidade a partir dos dados recebidos da API (DTO).
    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        try {
            this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        } catch (NumberFormatException e) {
            this.avaliacao = 0.0;
        }
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = ConsultaMyMemory.obterTraducao(dadosSerie.sinopse()).trim();
    }

    // GETTERS E SETTERS...
    public Long getId() {
        return id;
    }

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
        return  "Gênero: " + genero +
                ", Título: '" + titulo + '\'' +
                ", Total de Temporadas: " + totalTemporadas +
                ", Avaliação: " + avaliacao +
                ", Atores: '" + atores + '\'' +
                ", Pôster: '" + poster + '\'' +
                ", Sinopse: '" + sinopse + '\'';
    }
}