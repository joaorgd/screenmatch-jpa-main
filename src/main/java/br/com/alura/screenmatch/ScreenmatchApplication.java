package br.com.alura.screenmatch;

import br.com.alura.screenmatch.principal.Principal;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	// @Autowired: Anotação do Spring para injeção de dependência.
	// O Spring irá procurar uma implementação de SerieRepository (que ele mesmo cria em tempo de execução)
	// e a injetará automaticamente nesta variável.
	@Autowired
	private SerieRepository repositorio;

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	// O método run é executado assim que a aplicação Spring sobe.
	// É o ponto de entrada para a nossa lógica de negócio.
	@Override
	public void run(String... args) throws Exception {
		// Passa o repositório (que foi injetado pelo Spring) para a classe Principal.
		// Isso permite que a classe Principal interaja com o banco de dados.
		Principal principal = new Principal(repositorio);
		principal.exibeMenu();
	}
}