package br.com.alura.screenmatch.service.traducao;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// DTO para mapear os dados da resposta da tradução, que estão aninhados no JSON.
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosResposta(@JsonAlias(value = "translatedText") String textoTraduzido) {
}