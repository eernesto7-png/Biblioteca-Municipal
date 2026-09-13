import java.util.Scanner;

/**
 * Sistema de Gestão de Biblioteca
 * -------------------------------
 * Programa em Java que simula uma base de dados em memória (usando arrays e
 * uma matriz) para permitir o registo de livros, consulta de catálogo,
 * gestão de utilizadores e operações de empréstimo/devolução.
 *
 * Estruturas de dados principais:
 *  - livros[]            -> array de objetos Livro (catálogo)
 *  - utilizadores[]       -> array de objetos Utilizador (leitores registados)
 *  - matrizEmprestimos[][] -> matriz [livro][utilizador] com o número de vezes
 *                             que cada utilizador requisitou cada livro
 *  - emprestimosAtivos[]  -> array de objetos Emprestimo (empréstimos em curso)
 *
 * Compilar:  javac SistemaBiblioteca.java
 * Executar:  java SistemaBiblioteca
 */
public class BibliotecaMunicipal {

    // ---------------------------------------------------------------
    // Classes de domínio
    // ---------------------------------------------------------------

    static class Livro {
        int id;
        String titulo;
        String autor;
        int ano;
        int quantidadeTotal;
        int quantidadeDisponivel;
        int vezesEmprestado; // contador para estatísticas

        Livro(int id, String titulo, String autor, int ano, int quantidade) {
            this.id = id;
            this.titulo = titulo;
            this.autor = autor;
            this.ano = ano;
            this.quantidadeTotal = quantidade;
            this.quantidadeDisponivel = quantidade;
            this.vezesEmprestado = 0;
        }

        @Override
        public String toString() {
            return String.format("ID:%-4d | %-30s | %-20s | %d | Disponíveis: %d/%d",
                    id, titulo, autor, ano, quantidadeDisponivel, quantidadeTotal);
        }
    }

    static class Utilizador {
        int id;
        String nome;
        String email;

        Utilizador(int id, String nome, String email) {
            this.id = id;
            this.nome = nome;
            this.email = email;
        }

        @Override
        public String toString() {
            return String.format("ID:%-4d | %-25s | %s", id, nome, email);
        }
    }

    static class Emprestimo {
        int idLivro;
        int idUtilizador;
        boolean ativo; // true = ainda não devolvido

        Emprestimo(int idLivro, int idUtilizador) {
            this.idLivro = idLivro;
            this.idUtilizador = idUtilizador;
            this.ativo = true;
        }
    }

    // ---------------------------------------------------------------
    // "Base de dados" em memória (arrays de tamanho fixo)
    // ---------------------------------------------------------------

    static final int MAX_LIVROS = 200;
    static final int MAX_UTILIZADORES = 200;
    static final int MAX_EMPRESTIMOS = 500;

    static Livro[] livros = new Livro[MAX_LIVROS];
    static int totalLivros = 0;

    static Utilizador[] utilizadores = new Utilizador[MAX_UTILIZADORES];
    static int totalUtilizadores = 0;

    static Emprestimo[] emprestimos = new Emprestimo[MAX_EMPRESTIMOS];
    static int totalEmprestimos = 0; // inclui ativos e já devolvidos (histórico)

    // Matriz que cruza livros x utilizadores: número de requisições de cada
    // utilizador (coluna) a cada livro (linha). Usa índices posicionais
    // (posição no array livros / utilizadores), não o "id" do registo.
    static int[][] matrizEmprestimos = new int[MAX_LIVROS][MAX_UTILIZADORES];

    static int proximoIdLivro = 1;
    static int proximoIdUtilizador = 1;

    static Scanner scanner = new Scanner(System.in);

    // ---------------------------------------------------------------
    // Main / Menu
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        int opcao;
        do {
            mostrarMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1 -> registarLivro();
                case 2 -> listarLivros();
                case 3 -> pesquisarLivros();
                case 4 -> registarUtilizador();
                case 5 -> listarUtilizadores();
                case 6 -> efetuarEmprestimo();
                case 7 -> efetuarDevolucao();
                case 8 -> listarEmprestimosAtivos();
                case 9 -> mostrarEstatisticas();
                case 0 -> System.out.println("A encerrar o sistema. Até breve!");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 0);

        scanner.close();
    }

    static void mostrarMenu() {
        System.out.println("=========================================");
        System.out.println("   SISTEMA DE GESTÃO DE BIBLIOTECA");
        System.out.println("=========================================");
        System.out.println("1 - Registar novo livro");
        System.out.println("2 - Listar catálogo completo");
        System.out.println("3 - Pesquisar livro (por título ou autor)");
        System.out.println("4 - Registar novo utilizador");
        System.out.println("5 - Listar utilizadores");
        System.out.println("6 - Efetuar empréstimo");
        System.out.println("7 - Efetuar devolução");
        System.out.println("8 - Listar empréstimos ativos");
        System.out.println("9 - Estatísticas");
        System.out.println("0 - Sair");
        System.out.println("-----------------------------------------");
    }

    // ---------------------------------------------------------------
    // 1) Registo de Livros
    // ---------------------------------------------------------------

    static void registarLivro() {
        System.out.println("--- Registo de Novo Livro ---");

        if (totalLivros >= MAX_LIVROS) {
            System.out.println("Catálogo cheio. Não é possível registar mais livros.");
            return;
        }

        String titulo = lerTexto("Título: ");
        String autor = lerTexto("Autor: ");
        int ano = lerInteiro("Ano de publicação: ");
        int quantidade = lerInteiroPositivo("Quantidade disponível: ");

        Livro novo = new Livro(proximoIdLivro, titulo, autor, ano, quantidade);
        livros[totalLivros] = novo;
        totalLivros++;
        proximoIdLivro++;

        System.out.println("Livro registado com sucesso! " + novo);
    }

    // ---------------------------------------------------------------
    // 2) e 3) Consulta de Catálogo
    // ---------------------------------------------------------------

    static void listarLivros() {
        System.out.println("--- Catálogo de Livros ---");
        if (totalLivros == 0) {
            System.out.println("Não existem livros registados.");
            return;
        }
        for (int i = 0; i < totalLivros; i++) {
            System.out.println(livros[i]);
        }
    }

    static void pesquisarLivros() {
        System.out.println("--- Pesquisa de Livros ---");
        if (totalLivros == 0) {
            System.out.println("Não existem livros registados.");
            return;
        }

        System.out.println("Pesquisar por: 1) Título   2) Autor");
        int opcao = lerInteiro("Opção: ");
        String termo = lerTexto("Introduza o termo de pesquisa: ").toLowerCase();

        boolean encontrado = false;
        for (int i = 0; i < totalLivros; i++) {
            Livro l = livros[i];
            boolean corresponde = (opcao == 2)
                    ? l.autor.toLowerCase().contains(termo)
                    : l.titulo.toLowerCase().contains(termo);

            if (corresponde) {
                System.out.println(l);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("Nenhum livro encontrado com esse critério.");
        }
    }

    // ---------------------------------------------------------------
    // 4) e 5) Gestão de Utilizadores
    // ---------------------------------------------------------------

    static void registarUtilizador() {
        System.out.println("--- Registo de Novo Utilizador ---");

        if (totalUtilizadores >= MAX_UTILIZADORES) {
            System.out.println("Limite de utilizadores atingido.");
            return;
        }

        String nome = lerTexto("Nome: ");
        String email = lerTexto("Email: ");

        Utilizador novo = new Utilizador(proximoIdUtilizador, nome, email);
        utilizadores[totalUtilizadores] = novo;
        totalUtilizadores++;
        proximoIdUtilizador++;

        System.out.println("Utilizador registado com sucesso! " + novo);
    }

    static void listarUtilizadores() {
        System.out.println("--- Lista de Utilizadores ---");
        if (totalUtilizadores == 0) {
            System.out.println("Não existem utilizadores registados.");
            return;
        }
        for (int i = 0; i < totalUtilizadores; i++) {
            System.out.println(utilizadores[i]);
        }
    }

    // ---------------------------------------------------------------
    // Métodos auxiliares de procura por ID (devolvem a posição no array)
    // ---------------------------------------------------------------

    static int indiceLivroPorId(int id) {
        for (int i = 0; i < totalLivros; i++) {
            if (livros[i].id == id) return i;
        }
        return -1;
    }

    static int indiceUtilizadorPorId(int id) {
        for (int i = 0; i < totalUtilizadores; i++) {
            if (utilizadores[i].id == id) return i;
        }
        return -1;
    }

    // ---------------------------------------------------------------
    // 6) Empréstimo
    // ---------------------------------------------------------------

    static void efetuarEmprestimo() {
        System.out.println("--- Efetuar Empréstimo ---");

        if (totalLivros == 0 || totalUtilizadores == 0) {
            System.out.println("É necessário ter pelo menos um livro e um utilizador registados.");
            return;
        }

        if (totalEmprestimos >= MAX_EMPRESTIMOS) {
            System.out.println("Limite de empréstimos atingido.");
            return;
        }

        int idLivro = lerInteiro("ID do livro: ");
        int idxLivro = indiceLivroPorId(idLivro);
        if (idxLivro == -1) {
            System.out.println("Livro não encontrado.");
            return;
        }

        Livro livro = livros[idxLivro];
        if (livro.quantidadeDisponivel <= 0) {
            System.out.println("Não há exemplares disponíveis deste livro no momento.");
            return;
        }

        int idUtilizador = lerInteiro("ID do utilizador: ");
        int idxUtilizador = indiceUtilizadorPorId(idUtilizador);
        if (idxUtilizador == -1) {
            System.out.println("Utilizador não encontrado.");
            return;
        }

        // Atualiza a quantidade disponível
        livro.quantidadeDisponivel--;
        livro.vezesEmprestado++;

        // Regista o empréstimo ativo
        emprestimos[totalEmprestimos] = new Emprestimo(idLivro, idUtilizador);
        totalEmprestimos++;

        // Atualiza a matriz livro x utilizador
        matrizEmprestimos[idxLivro][idxUtilizador]++;

        System.out.println("Empréstimo registado: \"" + livro.titulo + "\" -> "
                + utilizadores[idxUtilizador].nome);
    }

    // ---------------------------------------------------------------
    // 7) Devolução
    // ---------------------------------------------------------------

    static void efetuarDevolucao() {
        System.out.println("--- Efetuar Devolução ---");

        listarEmprestimosAtivos();

        if (contarEmprestimosAtivos() == 0) {
            return;
        }

        int idLivro = lerInteiro("ID do livro a devolver: ");
        int idUtilizador = lerInteiro("ID do utilizador que devolve: ");

        // Procura o empréstimo ativo correspondente (o mais antigo em aberto)
        int idxEmprestimo = -1;
        for (int i = 0; i < totalEmprestimos; i++) {
            Emprestimo e = emprestimos[i];
            if (e.ativo && e.idLivro == idLivro && e.idUtilizador == idUtilizador) {
                idxEmprestimo = i;
                break;
            }
        }

        if (idxEmprestimo == -1) {
            System.out.println("Não foi encontrado um empréstimo ativo com esses dados.");
            return;
        }

        emprestimos[idxEmprestimo].ativo = false;

        int idxLivro = indiceLivroPorId(idLivro);
        if (idxLivro != -1) {
            livros[idxLivro].quantidadeDisponivel++;
            System.out.println("Devolução registada com sucesso: \"" + livros[idxLivro].titulo + "\"");
        } else {
            System.out.println("Devolução registada (o livro já não existe no catálogo).");
        }
    }

    static int contarEmprestimosAtivos() {
        int contador = 0;
        for (int i = 0; i < totalEmprestimos; i++) {
            if (emprestimos[i].ativo) contador++;
        }
        if (contador == 0) {
            System.out.println("Não existem empréstimos ativos no momento.");
        }
        return contador;
    }

    static void listarEmprestimosAtivos() {
        System.out.println("--- Empréstimos Ativos ---");
        boolean existeAlgum = false;
        for (int i = 0; i < totalEmprestimos; i++) {
            Emprestimo e = emprestimos[i];
            if (e.ativo) {
                existeAlgum = true;
                int idxLivro = indiceLivroPorId(e.idLivro);
                int idxUtilizador = indiceUtilizadorPorId(e.idUtilizador);
                String tituloLivro = (idxLivro != -1) ? livros[idxLivro].titulo : "(livro removido)";
                String nomeUtilizador = (idxUtilizador != -1) ? utilizadores[idxUtilizador].nome : "(utilizador removido)";
                System.out.printf("Livro ID:%d \"%s\"  ->  Utilizador ID:%d %s%n",
                        e.idLivro, tituloLivro, e.idUtilizador, nomeUtilizador);
            }
        }
        if (!existeAlgum) {
            System.out.println("(nenhum)");
        }
    }

    // ---------------------------------------------------------------
    // 9) Estatísticas
    // ---------------------------------------------------------------

    static void mostrarEstatisticas() {
        System.out.println("--- Estatísticas da Biblioteca ---");

        if (totalLivros == 0) {
            System.out.println("Não existem livros registados.");
            return;
        }

        // Livro mais emprestado (percorrendo o array de livros)
        int indiceMaisEmprestado = 0;
        for (int i = 1; i < totalLivros; i++) {
            if (livros[i].vezesEmprestado > livros[indiceMaisEmprestado].vezesEmprestado) {
                indiceMaisEmprestado = i;
            }
        }

        Livro maisEmprestado = livros[indiceMaisEmprestado];
        if (maisEmprestado.vezesEmprestado > 0) {
            System.out.println("Livro mais requisitado: \"" + maisEmprestado.titulo
                    + "\" (" + maisEmprestado.vezesEmprestado + " empréstimo(s))");
        } else {
            System.out.println("Ainda não foi efetuado nenhum empréstimo.");
        }

        // Número total de livros requisitados (histórico completo)
        System.out.println("Número total de empréstimos efetuados (histórico): " + totalEmprestimos);
        System.out.println("Número de empréstimos atualmente em curso: " + contarEmprestimosAtivosSemMensagem());

        // Exemplo de uso da matriz: utilizador que mais vezes requisitou o
        // livro mais emprestado, percorrendo a linha correspondente da matriz.
        if (maisEmprestado.vezesEmprestado > 0 && totalUtilizadores > 0) {
            int melhorUtilizador = 0;
            for (int j = 1; j < totalUtilizadores; j++) {
                if (matrizEmprestimos[indiceMaisEmprestado][j] > matrizEmprestimos[indiceMaisEmprestado][melhorUtilizador]) {
                    melhorUtilizador = j;
                }
            }
            int vezes = matrizEmprestimos[indiceMaisEmprestado][melhorUtilizador];
            if (vezes > 0) {
                System.out.println("Utilizador que mais requisitou esse livro: "
                        + utilizadores[melhorUtilizador].nome + " (" + vezes + " vez(es))");
            }
        }
    }

    static int contarEmprestimosAtivosSemMensagem() {
        int contador = 0;
        for (int i = 0; i < totalEmprestimos; i++) {
            if (emprestimos[i].ativo) contador++;
        }
        return contador;
    }

    // ---------------------------------------------------------------
    // Utilitários de leitura de input com validação
    // ---------------------------------------------------------------

    static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    static int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String linha = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Introduza um número inteiro.");
            }
        }
    }

    static int lerInteiroPositivo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor >= 0) return valor;
            System.out.println("O valor não pode ser negativo.");
        }
    }
}