package br.com.senac.tarefas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarTarefaRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
        String titulo,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao) {
}