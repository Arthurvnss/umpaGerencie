package br.com.senac.tarefas.dto;

import br.com.senac.tarefas.model.StatusTarefa;
import br.com.senac.tarefas.model.Tarefa;
import java.time.LocalDateTime;

public record AtividadeResponse(
        Long id,
        String titulo,
        String descricao,
        StatusTarefa status,
        LocalDateTime dataCriacao,
        LocalDateTime dataConclusao) {

    public static AtividadeResponse de(Atividade atividade) {
        return new AtividadeResponse(
                atividade.getId(),
                atividade.getTitulo(),
                atividade.getDescricao(),
                atividade.getStatus(),
                atividade.getDataCriacao(),
                atividade.getDataConclusao());
    }
}