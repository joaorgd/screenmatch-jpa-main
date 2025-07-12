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

    private List<Serie> seriesBuscadas = new ArrayList<>(); // Renomeado para clareza

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
                    4 - Buscar série por título
                    
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
                case 4:
                    buscarSeriePorTitulo();
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

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("\nDigite o nome da série da qual deseja ver os episódios:");
        var nomeSerie = leitura.nextLine();

        // A busca no repositório agora retorna uma lista de possíveis séries.
        List<Serie> seriesEncontradas = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        // Se mais de uma série for encontrada, o ideal seria pedir para o usuário especificar.
        // Para simplificar, vamos pegar a primeira da lista, mas avisando o usuário.
        if (!seriesEncontradas.isEmpty()) {
            Serie serieAchada = seriesEncontradas.get(0); // Pega o primeiro resultado da busca
            if (seriesEncontradas.size() > 1) {
                System.out.println("Múltiplas séries encontradas, processando a primeira: " + serieAchada.getTitulo());
            }

            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieAchada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieAchada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
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
        System.out.println("\nSéries salvas no banco de dados:");
        seriesBuscadas.sort(Comparator.comparing(Serie::getGenero));
        seriesBuscadas.forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite um trecho do título da série que deseja buscar: ");
        var nomeSerie = leitura.nextLine();

        // A busca agora retorna uma lista, evitando o erro.
        List<Serie> seriesEncontradas = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (!seriesEncontradas.isEmpty()) {
            System.out.println("Séries encontradas:");
            // Mostra todas as séries que correspondem à busca.
            seriesEncontradas.forEach(System.out::println);
        } else {
            System.out.println("Nenhuma série encontrada com este título no banco de dados.");
        }
    }
}