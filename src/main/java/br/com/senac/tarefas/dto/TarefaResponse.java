package br.com.senac.tarefas.dto;

import br.com.senac.tarefas.model.StatusTarefa;
import br.com.senac.tarefas.model.Tarefa;
import java.time.LocalDateTime;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        StatusTarefa status,
        LocalDateTime dataCriacao,
        LocalDateTime dataConclusao) {

    public static TarefaResponse de(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getDataCriacao(),
                tarefa.getDataConclusao());
    }
}