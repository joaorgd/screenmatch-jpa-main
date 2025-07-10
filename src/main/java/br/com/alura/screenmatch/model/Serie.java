package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.traducao.ConsultaMyMemory;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

// @Entity informa ao JPA que esta classe corresponde a uma tabela no banco de dados.
@Entity
// @Table especifica o nome da tabela. Se omitido, o nome da classe seria usado.
@Table(name = "series")
public class Serie {
    // @Id marca este campo como a chave primária da tabela.
    @Id
    // @GeneratedValue define a estratégia de geração automática da chave primária.
    // GenerationType.IDENTITY delega a criação do valor para o banco de dados (ex: auto-incremento).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(unique = true) cria uma restrição na tabela para não haver títulos duplicados.
    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    // @Enumerated(EnumType.STRING) salva o valor do enum como texto (ex: "ACAO") no banco,
    // tornando os dados mais legíveis do que o padrão numérico.
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    // @Transient indica ao JPA para ignorar este campo. Ele não será salvo no banco de dados
    // e existirá apenas em memória, no objeto Java.
    @Transient
    private List<Episodio> episodios = new ArrayList<>();

    // Construtor padrão (vazio) é obrigatório para que o JPA possa criar instâncias desta classe.
    public Serie() {}

    // Construtor utilizado para converter os dados brutos da API (DTO) numa entidade Serie.
    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        try {
            this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0.0);
        } catch (NumberFormatException e) {
            this.avaliacao = 0.0;
        }
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = ConsultaMyMemory.obterTraducao(dadosSerie.sinopse()).trim();
    }

    // Getters e Setters são métodos de acesso aos atributos da classe.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        this.episodios = episodios;
    }

    // O método toString() é usado para obter uma representação textual do objeto,
    // útil para depuração e exibição no console.
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