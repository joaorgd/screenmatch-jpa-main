package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

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
    private final String API_KEY = "&apikey=6585022c"; // Use a sua própria chave da API

    // Lista para armazenar as séries que buscamos, já convertidas para nosso modelo de domínio.
    // A aplicação opera com base nesta lista de objetos 'Serie'.
    private List<Serie> series = new ArrayList<>();

    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios por série
                    3 - Listar séries buscadas
                    
                    0 - Sair
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
        // Converte os dados brutos da API (DadosSerie) para o nosso modelo de domínio (Serie).
        // A conversão é feita imediatamente após a busca para centralizar a lógica.
        Serie serie = new Serie(dados);
        // Adiciona a série já convertida à nossa lista principal.
        //series.add(serie);
        repositorio.save(serie);
        System.out.println("\nSérie adicionada com sucesso!");
        System.out.println(serie);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
        // Este método tem a responsabilidade única de buscar e desserializar os dados brutos da API.
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("\nEscolha uma série pelo nome para buscar os episódios:");
        var nomeSerie = leitura.nextLine();

        // Busca na nossa lista de séries. Como a lista já é de objetos 'Serie',
        // podemos usar diretamente os métodos da classe, como 'getTitulo()'.
        // O 'Optional' é usado para evitar erros caso a série não seja encontrada.
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
            System.out.println("Série não encontrada na lista!");
        }
    }

    private void listarSeriesBuscadas() {
        // A lógica de listagem se torna muito mais simples.
        // Como a lista principal já contém objetos 'Serie' formatados,
        // basta ordenar a lista e depois imprimir cada elemento.
        System.out.println("\nSéries buscadas, ordenadas por gênero:");
        series.sort(Comparator.comparing(Serie::getGenero));
        series.forEach(System.out::println);
    }
}