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

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

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
            leitura.nextLine();

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

    // MÉTODO CORRIGIDO E REATORADO
    private void buscarEpisodioPorSerie() {
        // Agora não precisamos mais carregar todas as séries em memória primeiro.
        // A busca é feita diretamente no banco de dados.
        System.out.println("\nDigite o nome da série da qual deseja ver os episódios:");
        var nomeSerie = leitura.nextLine();

        // Utiliza o método do repositório para buscar no banco de forma eficiente.
        Optional<Serie> serieEncontrada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieEncontrada.isPresent()) {
            Serie serieAchada = serieEncontrada.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            // Busca os dados de todas as temporadas na API
            for (int i = 1; i <= serieAchada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieAchada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            System.out.println("\nTemporadas de " + serieAchada.getTitulo() + " encontradas na API:");
            temporadas.forEach(System.out::println);

            // Transforma os dados dos episódios em entidades e associa à série
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieAchada.setEpisodios(episodios);
            // Salva a série com a lista de episódios atualizada, graças ao Cascade.
            repositorio.save(serieAchada);

            System.out.println("\nEpisódios salvos no banco para a série: " + serieAchada.getTitulo());

        } else {
            System.out.println("Série não encontrada no banco de dados!");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        System.out.println("\nSéries salvas no banco de dados:");
        series.sort(Comparator.comparing(Serie::getGenero));
        series.forEach(System.out::println);
    }
}