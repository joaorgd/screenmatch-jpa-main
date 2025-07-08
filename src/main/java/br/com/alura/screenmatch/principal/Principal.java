package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c"; // Use sua própria chave da API

    // A lista principal agora armazena objetos do tipo Serie, nosso modelo de domínio.
    // Isso centraliza a conversão de dados no momento da busca.
    private List<Serie> series = new ArrayList<>();

    public void exibeMenu() {
        //... (código do menu permanece igual)
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        // Converte o DTO (DadosSerie) para a entidade de domínio (Serie)
        Serie serie = new Serie(dados);
        series.add(serie);
        System.out.println("Série adicionada com sucesso!");
        System.out.println(serie);
    }

    private DadosSerie getDadosSerie() {
        //... (código do getDadosSerie permanece igual)
    }

    t   private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("\nEscolha uma série pelo nome para buscar os episódios:");
        var nomeSerie = leitura.nextLine();

        // Optional para lidar com a possibilidade da série não ser encontrada.
        var serieEncontrada = series.stream()
                .filter(s -> s.getTitulo().equalsIgnoreCase(nomeSerie))
                .findFirst();

        if (serieEncontrada.isPresent()) {
            var dadosSerie = serieEncontrada.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= dadosSerie.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + dadosSerie.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);
        } else {
            System.out.println("Série não encontrada na lista!");
        }
    }

    private void listarSeriesBuscadas() {
        System.out.println("\nSéries buscadas, ordenadas por gênero:");

        // A lógica de ordenação agora é mais simples e direta,
        // pois a lista já contém objetos do tipo Serie.
        series.sort(Comparator.comparing(Serie::getGenero));
        series.forEach(System.out::println);
    }
}