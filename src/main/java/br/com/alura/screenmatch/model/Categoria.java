package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime"),
    AVENTURA("Adventure"),
    ANIMAÇÃO("Animation");

    // Atributo para armazenar o valor de texto correspondente ao da API.
    private String categoriaOmdb;

    // Construtor do enum, chamado para cada constante definida acima.
    Categoria(String categoriaOmdb) {
        this.categoriaOmdb = categoriaOmdb;
    }

    // Método estático para converter o texto da API em um tipo enum.
    // Isso centraliza a lógica de conversão e garante a criação de tipos válidos.
    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            // Compara o texto recebido com o valor de cada enum, ignorando maiúsculas/minúsculas.
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        // Lança uma exceção se o texto não corresponder a nenhuma categoria mapeada.
        // Isso evita a criação de dados inválidos e ajuda a identificar novos gêneros da API.
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}