package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c"; // Use sua própria chave da API

    // Esta lista em memória ainda existe, mas seu papel está diminuindo.
    // O ideal é que todas as operações de leitura passem a usar o repositório.
    private List<Serie> series = new ArrayList<>();

    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    *************************************************
                    1 - Salvar nova série no banco
                    2 - Buscar episódios de uma série
                    3 - Listar séries salvas no banco
                    
                    0 - Sair
                    *************************************************
                    """;

            System.out.println(menu);
            System.out.print("Digite sua opção: ");
            opcao = leitura.nextInt();
            leitura.nextLine(); // Consome a nova linha que o nextInt() deixa

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        // O método .save() persiste a entidade no banco de dados.
        repositorio.save(serie);
        System.out.println("\nSérie salva no banco de dados com sucesso!");
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        // Para este método funcionar corretamente, primeiro listamos as séries do banco.
        listarSeriesBuscadas();
        System.out.println("\nDigite o nome da série da qual deseja ver os episódios:");
        var nomeSerie = leitura.nextLine();

        // Opcional: buscar no banco de dados em vez da lista em memória
        // Optional<Serie> serieEncontrada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        // A busca atual ainda ocorre na lista em memória 'series', que não reflete o banco inteiro.
        // O ideal é refatorar para buscar do repositório.
        Optional<Serie> serieEncontrada = series.stream()
                .filter(s -> s.getTitulo().equalsIgnoreCase(nomeSerie))
                .findFirst();

        if (serieEncontrada.isPresent()) {
            var serieAchada = serieEncontrada.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieAchada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieAchada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            System.out.println("\nEpisódios da série: " + serieAchada.getTitulo());
            temporadas.forEach(System.out::println);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        // PONTO-CHAVE: Leitura de dados do banco.
        // O método `findAll()` do JpaRepository executa um 'SELECT *' na tabela de séries
        // e retorna uma lista de todas as entidades 'Serie' persistidas.
        series = repositorio.findAll();

        System.out.println("\nSéries salvas no banco de dados:");
        // A ordenação agora é feita na lista de séries que acabamos de buscar do banco.
        series.sort(Comparator.comparing(Serie::getGenero));
        series.forEach(System.out::println);
    }
}