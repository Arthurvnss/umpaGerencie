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
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public TarefaResponse criar(CriarTarefaRequest request) {
        if (tarefaRepository.existsByTituloIgnoreCaseAndStatusNot(
                request.titulo().trim(), StatusTarefa.CONCLUIDA)) {
            throw new RegraDeNegocioException("Já existe uma tarefa ativa com esse título");
        }

        Tarefa tarefa = new Tarefa(
                request.titulo().trim(), request.descricao(), StatusTarefa.PENDENTE);
        return TarefaResponse.de(tarefaRepository.save(tarefa));
    }

    @Transactional(readOnly = true)
    public List<TarefaResponse> listar() {
        return tarefaRepository.findAll().stream().map(TarefaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public TarefaResponse buscarPorId(Long id) {
        return TarefaResponse.de(buscarEntidade(id));
    }

    public TarefaResponse atualizar(Long id, AtualizarTarefaRequest request) {
        Tarefa tarefa = buscarEntidade(id);
        StatusTarefa statusAnterior = tarefa.getStatus();

        if (statusAnterior == StatusTarefa.PENDENTE
                && request.status() == StatusTarefa.CONCLUIDA) {
            throw new RegraDeNegocioException(
                    "Uma tarefa pendente deve passar por EM_ANDAMENTO antes de ser concluída");
        }

        String titulo = request.titulo().trim();
        if (request.status() != StatusTarefa.CONCLUIDA
                && tarefaRepository.existsByTituloIgnoreCaseAndStatusNotAndIdNot(
                        titulo, StatusTarefa.CONCLUIDA, id)) {
            throw new RegraDeNegocioException("Já existe uma tarefa ativa com esse título");
        }

        tarefa.setTitulo(titulo);
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(request.status());
        tarefa.setDataConclusao(request.status() == StatusTarefa.CONCLUIDA
                ? tarefa.getDataConclusao() == null ? LocalDateTime.now() : tarefa.getDataConclusao()
                : null);
        return TarefaResponse.de(tarefaRepository.save(tarefa));
    }

    public void excluir(Long id) {
        Tarefa tarefa = buscarEntidade(id);
        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA) {
            throw new RegraDeNegocioException("Não é permitido excluir uma tarefa concluída");
        }
        tarefaRepository.delete(tarefa);
    }

    private Tarefa buscarEntidade(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Tarefa não encontrada com id " + id));
    }
}