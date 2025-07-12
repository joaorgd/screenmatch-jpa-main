package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumo = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private List<Serie> seriesBuscadas = new ArrayList<>();

    private final SerieRepository repositorio;

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
                    4 - Buscar série por título
                    5 - Buscar série por ator
                    6 - Buscar Top 5 séries
                    
                    0 - Sair
                    *************************************************
                    """;

            System.out.println(menu);
            System.out.print("Digite sua opção: ");
            opcao = leitura.nextInt();
            leitura.nextLine(); // Limpa o buffer do scanner

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
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6: // Nova opção de menu
                    buscarTop5Series();
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
        repositorio.save(serie);
        System.out.println("\nSérie salva no banco de dados com sucesso!");
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("\nDigite um trecho do nome da série da qual deseja ver os episódios:");
        var nomeSerie = leitura.nextLine();

        List<Serie> seriesEncontradas = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (!seriesEncontradas.isEmpty()) {
            Serie serieAchada = seriesEncontradas.get(0);
            if (seriesEncontradas.size() > 1) {
                System.out.println("Múltiplas séries encontradas, processando a primeira: " + serieAchada.getTitulo());
            }

            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieAchada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieAchada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                temporadas.add(conversor.obterDados(json, DadosTemporada.class));
            }

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieAchada.setEpisodios(episodios);
            repositorio.save(serieAchada);

            System.out.println("\nEpisódios de '" + serieAchada.getTitulo() + "' salvos no banco!");
        } else {
            System.out.println("Série não encontrada no banco de dados!");
        }
    }

    private void listarSeriesBuscadas() {
        seriesBuscadas = repositorio.findAll();
        seriesBuscadas.sort(Comparator.comparing(Serie::getGenero));
        System.out.println("\nSéries salvas no banco de dados (ordenadas por gênero):");
        seriesBuscadas.forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite um trecho do título da série que deseja buscar: ");
        var nomeSerie = leitura.nextLine();
        List<Serie> seriesEncontradas = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (!seriesEncontradas.isEmpty()) {
            System.out.println("\nSéries encontradas:");
            seriesEncontradas.forEach(System.out::println);
        } else {
            System.out.println("\nNenhuma série encontrada com este título no banco de dados.");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome do ator para busca?");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de qual valor?");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        if (!seriesEncontradas.isEmpty()) {
            System.out.println("\nSéries em que '" + nomeAtor + "' trabalhou (avaliação >= " + avaliacao + "):");
            seriesEncontradas.forEach(s ->
                    System.out.println("  - " + s.getTitulo() + " (Avaliação: " + s.getAvaliacao() + ")"));
        } else {
            System.out.println("\nNenhuma série encontrada para '" + nomeAtor + "' com os critérios informados.");
        }
    }

    /**
     * Busca as 5 séries com as melhores avaliações no banco de dados e as exibe.
     */
    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByAvaliacaoDesc();
        System.out.println("\nTop 5 melhores séries no banco de dados:");
        topSeries.forEach(s ->
                System.out.println("  - " + s.getTitulo() + " (Avaliação: " + s.getAvaliacao() + ")"));
    }
}