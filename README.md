# Gerenciador de Tarefas

API REST em Java 17 e Spring Boot para cadastrar e gerenciar tarefas, com persistência em H2 em memória.

## Executar

Requisitos: JDK 17 ou superior e Maven 3.6 ou superior.

```bash
mvn spring-boot:run
```

A API fica disponível em `http://localhost:8080/api/tarefas`. O console H2 fica em `http://localhost:8080/h2-console`, usando JDBC URL `jdbc:h2:mem:testdb`, usuário `sa` e senha vazia.

## Testes

```bash
mvn test
```

A suíte contém testes unitários do service com JUnit 5/Mockito e testes de integração HTTP com MockMvc.

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | `/api/tarefas` | Cria tarefa; recebe `titulo` e, opcionalmente, `descricao` |
| GET | `/api/tarefas` | Lista tarefas |
| GET | `/api/tarefas/{id}` | Busca por identificador |
| PUT | `/api/tarefas/{id}` | Atualiza título, descrição e status |
| DELETE | `/api/tarefas/{id}` | Exclui tarefa não concluída |

Para atualizar, envie `titulo`, `descricao` (opcional) e `status` (`PENDENTE`, `EM_ANDAMENTO` ou `CONCLUIDA`). A criação inicia em `PENDENTE`; a conclusão de uma tarefa em andamento registra `dataConclusao` automaticamente.

## Regras de negócio

- Título obrigatório entre 5 e 100 caracteres; descrição de até 255 caracteres.
- Não há títulos repetidos entre tarefas ativas, sem distinção entre maiúsculas e minúsculas. Tarefas concluídas não bloqueiam o uso do título.
- Uma tarefa `PENDENTE` precisa passar por `EM_ANDAMENTO` antes de ser concluída.
- Tarefas concluídas não podem ser excluídas.
- Erros de validação e regras de negócio retornam HTTP 400; recursos inexistentes retornam HTTP 404.