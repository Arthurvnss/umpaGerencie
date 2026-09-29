package br.com.senac.tarefas.service;

import br.com.senac.tarefas.dto.AtualizarTarefaRequest;
import br.com.senac.tarefas.dto.CriarTarefaRequest;
import br.com.senac.tarefas.dto.TarefaResponse;
import br.com.senac.tarefas.exception.RecursoNaoEncontradoException;
import br.com.senac.tarefas.exception.RegraDeNegocioException;
import br.com.senac.tarefas.model.StatusTarefa;
import br.com.senac.tarefas.model.Tarefa;
import br.com.senac.tarefas.repository.TarefaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;

    public AtividadeService(AtividadeRepository atividadeRepository) {
        this.atividadeRepository = atividadeRepository;
    }

    public atividadeResponse criar(CriarAtividadeRequest request) {
        if (atividadeRepository.existsByTituloIgnoreCaseAndStatusNot(
                request.titulo().trim(), StatusAtividade.CONCLUIDA)) {
            throw new RegraDeNegocioException("Já existe uma atividade ativa com esse título");
        }

        Atividade atividade = new Atividade(
                request.titulo().trim(), request.descricao(), StatusAtividade.PENDENTE);
        return AtividadeResponse.de(atividadeRepository.save(atividade));
    }

    @Transactional(readOnly = true)
    public List<AtividadeResponse> listar() {
        return atividadeRepository.findAll().stream().map(AtividadeResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public AtividadeResponse buscarPorId(Long id) {
        return AtividadeResponse.de(buscarEntidade(id));
    }

    public AtividadeResponse atualizar(Long id, AtualizarAtividadeRequest request) {
        Atividade atividade = buscarEntidade(id);
        StatusAtividade statusAnterior = atividade.getStatus();

        if (statusAnterior == StatusAtividade.PENDENTE
                && request.status() == StatusAtividade.CONCLUIDA) {
            throw new RegraDeNegocioException(
                    "Uma atividade pendente deve passar por EM_ANDAMENTO antes de ser concluída");
        }

        String titulo = request.titulo().trim();
        if (request.status() != StatusAtividade.CONCLUIDA
                && atividadeRepository.existsByTituloIgnoreCaseAndStatusNotAndIdNot(
                        titulo, StatusAtividade.CONCLUIDA, id)) {
            throw new RegraDeNegocioException("Já existe uma atividade ativa com esse título");
        }

        atividade.setTitulo(titulo);
        atividade.setDescricao(request.descricao());
        atividade.setStatus(request.status());
        atividade.setDataConclusao(request.status() == StatusAtividade.CONCLUIDA
                ? atividade.getDataConclusao() == null ? LocalDateTime.now() : atividade.getDataConclusao()
                : null);
        return AtividadeResponse.de(AtividadeRepository.save(atividade));
    }

    public void excluir(Long id) {
        Atividade atividade = buscarEntidade(id);
        if (atividade.getStatus() == StatusAtividade.CONCLUIDA) {
            throw new RegraDeNegocioException("Não é permitido excluir uma atividade concluída");
        }
        atividadeRepository.delete(atividade);
    }

    private Atividade buscarEntidade(Long id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Atividade não encontrada com id " + id));
    }
}