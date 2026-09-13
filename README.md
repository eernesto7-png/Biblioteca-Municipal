# Sistema de Gestão de Biblioteca

Programa em **Java** para consola que permite aos bibliotecários registar obras, consultar o catálogo, gerir utilizadores e efetuar operações de empréstimo e devolução de forma automatizada e fiável.

Os dados são mantidos numa **base de dados simulada em memória**, usando **arrays** e uma **matriz**, sem necessidade de base de dados externa.

## Funcionalidades

- **Registo de Livros** — inserir novos títulos no catálogo (ID único gerado automaticamente, título, autor, ano de publicação e quantidade disponível).
- **Consulta de Catálogo** — listar todos os livros ou pesquisar por título/autor.
- **Gestão de Utilizadores** — registar e listar utilizadores da biblioteca.
- **Empréstimos** — emprestar um livro a um utilizador registado, com validação de disponibilidade e decremento automático do stock.
- **Devoluções** — registar a devolução de um livro, repondo a quantidade disponível.
- **Estatísticas** — livro mais emprestado, número total de empréstimos realizados (histórico) e empréstimos atualmente em curso.

## Estruturas de dados

| Estrutura | Tipo | Descrição |
|---|---|---|
| `livros[]` | `Livro[]` | Catálogo de livros (array) |
| `utilizadores[]` | `Utilizador[]` | Utilizadores registados (array) |
| `emprestimos[]` | `Emprestimo[]` | Histórico de empréstimos (array) |
| `matrizEmprestimos[][]` | `int[][]` | Matriz livro × utilizador com o nº de requisições de cada utilizador a cada livro |

Cada `Livro` guarda: `id`, `titulo`, `autor`, `ano`, `quantidadeTotal`, `quantidadeDisponivel`, `vezesEmprestado`.
Cada `Utilizador` guarda: `id`, `nome`, `email`.
Cada `Emprestimo` guarda: `idLivro`, `idUtilizador`, `ativo` (por devolver ou já devolvido).

## Requisitos

- **JDK 17 ou superior** (o programa usa `switch` com expressões `->`)

## Como compilar e executar

```bash
javac SistemaBiblioteca.java
java SistemaBiblioteca
```

## Utilização

Ao executar o programa, é apresentado um menu interativo:

```
=========================================
   SISTEMA DE GESTÃO DE BIBLIOTECA
=========================================
1 - Registar novo livro
2 - Listar catálogo completo
3 - Pesquisar livro (por título ou autor)
4 - Registar novo utilizador
5 - Listar utilizadores
6 - Efetuar empréstimo
7 - Efetuar devolução
8 - Listar empréstimos ativos
9 - Estatísticas
0 - Sair
-----------------------------------------
```

Basta introduzir o número da opção pretendida e seguir as instruções apresentadas.

### Exemplo de fluxo típico

1. Registar um ou mais livros (opção **1**).
2. Registar um ou mais utilizadores (opção **4**).
3. Efetuar um empréstimo indicando o ID do livro e o ID do utilizador (opção **6**).
4. Consultar os empréstimos em curso (opção **8**).
5. Registar a devolução do livro (opção **7**).
6. Consultar as estatísticas da biblioteca (opção **9**).

## Limitações e notas

- Os dados existem apenas **em memória**: ao fechar o programa, todo o catálogo, utilizadores e histórico de empréstimos são perdidos (não há persistência em ficheiro ou base de dados).
- A capacidade é limitada por constantes (`MAX_LIVROS = 200`, `MAX_UTILIZADORES = 200`, `MAX_EMPRESTIMOS = 500`), podendo ser ajustadas no código-fonte conforme necessário.
- O sistema assume um exemplar por empréstimo/devolução de cada vez.

## Possíveis melhorias futuras

- Persistência dos dados em ficheiro (texto, CSV ou base de dados).
- Interface gráfica (Swing/JavaFX) em vez de consola.
- Datas reais de empréstimo/devolução e cálculo de multas por atraso.
- Edição e remoção de livros e utilizadores.