package br.com.alura.screenmatch.service.traducao;

import br.com.alura.screenmatch.service.ConsumoApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ConsultaMyMemory {
    // Método estático, pode ser chamado sem criar uma instância da classe.
    public static String obterTraducao(String text) {
        ObjectMapper mapper = new ObjectMapper();
        ConsumoApi consumo = new ConsumoApi();

        // Codifica o texto e o par de idiomas para serem usados de forma segura na URL.
        String textoCodificado = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String langpairCodificado = URLEncoder.encode("en|pt-br", StandardCharsets.UTF_8);

        String url = "https://api.mymemory.translated.net/get?q=" + textoCodificado + "&langpair=" + langpairCodificado;

        String json = consumo.obterDados(url);

        DadosTraducao traducao;
        try {
            // Converte a string JSON recebida em objetos Java (DTOs).
            traducao = mapper.readValue(json, DadosTraducao.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Não foi possível processar o JSON da tradução.", e);
        }

        // Navega através dos objetos aninhados para obter o texto final.
        return traducao.dadosResposta().textoTraduzido();
    }
}