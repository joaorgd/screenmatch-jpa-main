package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c"; // Use sua própria chave da API

    // Esta lista armazena os dados das séries buscadas durante a execução do programa.
    // É uma solução temporária antes da implementação de um banco de dados.
    private List<DadosSerie> dadosSeries = new ArrayList<>();

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
        // Este método coordena a busca: primeiro obtém os dados (getDadosSerie),
        // depois os adiciona à lista em memória e, por fim, exibe na tela.
        DadosSerie dados = getDadosSerie();
        dadosSeries.add(dados);
        System.out.println(dados); // Chama o método toString() de DadosSerie para exibir.
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
        // O nome da série é usado para montar a URL completa, consumir a API
        // e, em seguida, usar o ConverteDados para transformar a resposta JSON em um objeto Java.
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome para buscar os episódios:");
        var nomeSerie = leitura.nextLine();

        // Em vez de ir à web de novo, o código usa a API de Streams do Java
        // para filtrar e encontrar a série desejada na lista `dadosSeries` que já está em memória.
        var serieEncontrada = dadosSeries.stream()
                .filter(s -> s.titulo().equalsIgnoreCase(nomeSerie))
                .findFirst();

        if (serieEncontrada.isPresent()) {
            var dadosSerie = serieEncontrada.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);
        } else {
            System.out.println("Série não encontrada na lista!");
        }
    }

    private void listarSeriesBuscadas() {
        System.out.println("\nSéries já buscadas:");
        // Ao percorrer a lista, o `System.out.println` invoca o `toString()` personalizado
        // de `DadosSerie`, mostrando agora todos os novos campos (gênero, atores, etc.).
        dadosSeries.forEach(System.out::println);
        System.out.println();
    }
}