package br.com.senac.atividades.dto;

import br.com.senac.atividades.model.StatusAtividades;
import br.com.senac.atividades.model.Atividades;
import java.time.LocalDateTime;

public record AtividadeResponse(
        Long id,
        String titulo,
        String descricao,
        StatusAtividades status,
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