package br.com.senac.tarefas.service;

import br.com.senac.tarefas.dto.AtualizarTarefaRequest;
import br.com.senac.tarefas.dto.CriarTarefaRequest;
import br.com.senac.tarefas.dto.TarefaResponse;
import br.com.senac.tarefas.exception.RecursoNaoEncontradoException;
import br.com.senac.tarefas.exception.RegraDeNegocioException;
import br.com.senac.tarefas.model.StatusTarefa;
import br.com.senac.tarefas.model.Tarefa;
import br.com.senac.tarefas.repository.TarefaRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtividadeServiceTest {

    @Mock
    private AtividadeRepository atividadeRepository;

    @InjectMocks
    private AtividadeService atividadeService;

    @Test
    void deveCriarAtividadePendente() {
        when(atividadeRepository.existsByTituloIgnoreCaseAndStatusNot(
                "Implementar endpoint", StatusAtividade.CONCLUIDA)).thenReturn(false);
        when(atividadeRepository.save(any(Atividade.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        AtividadeResponse resposta = atividadeService.criar(
                new CriarAtividadeRequest("Implementar endpoint", "API de atividades"));

        assertEquals("Implementar endpoint", resposta.titulo());
        assertEquals(StatusAtividade.PENDENTE, resposta.status());
        verify(atividadeRepository).save(any(Atividade.class));
    }

    @Test
    void deveLancarExcecaoAoBuscarAtividadeInexistente() {
        when(atividadeRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> atividadeService.buscarPorId(42L));
        verify(atividadeRepository).findById(42L);
    }

        @Test
        void deveBuscarAtividadePorId() {
               Atividade atividade = new Atividade("Consultar atividade", "Detalhes", StatusTarefa.PENDENTE);
                when(tarefaRepository.findById(42L)).thenReturn(Optional.of(tarefa));

                TarefaResponse resposta = tarefaService.buscarPorId(42L);

                assertEquals("Consultar tarefa", resposta.titulo());
                assertEquals(StatusTarefa.PENDENTE, resposta.status());
                verify(tarefaRepository).findById(42L);
        }

    @Test
    void deveAtualizarTarefaERegistrarDataConclusao() {
        Tarefa tarefa = new Tarefa("Revisar documentação", null, StatusTarefa.EM_ANDAMENTO);
        when(tarefaRepository.findById(7L)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.save(any(Tarefa.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        TarefaResponse resposta = tarefaService.atualizar(7L,
                new AtualizarTarefaRequest("Revisar documentação final", "Revisão concluída",
                        StatusTarefa.CONCLUIDA));

        assertEquals(StatusTarefa.CONCLUIDA, resposta.status());
        assertNotNull(resposta.dataConclusao());
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void naoDevePermitirConcluirTarefaPendenteDiretamente() {
        Tarefa tarefa = new Tarefa("Revisar documentação", null, StatusTarefa.PENDENTE);
        when(tarefaRepository.findById(7L)).thenReturn(Optional.of(tarefa));

        assertThrows(RegraDeNegocioException.class,
                () -> tarefaService.atualizar(7L,
                        new AtualizarTarefaRequest("Revisar documentação", null,
                                StatusTarefa.CONCLUIDA)));
        verify(tarefaRepository, never()).save(any(Tarefa.class));
    }

    @Test
    void naoDeveExcluirTarefaConcluida() {
        Tarefa tarefa = new Tarefa("Revisar documentação", null, StatusTarefa.CONCLUIDA);
        when(tarefaRepository.findById(7L)).thenReturn(Optional.of(tarefa));

        assertThrows(RegraDeNegocioException.class, () -> tarefaService.excluir(7L));
        verify(tarefaRepository, never()).delete(any(Tarefa.class));
    }

    @Test
    void naoDeveCriarTituloDuplicadoAtivo() {
        when(tarefaRepository.existsByTituloIgnoreCaseAndStatusNot(
                "Implementar endpoint", StatusTarefa.CONCLUIDA)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class,
                () -> tarefaService.criar(new CriarTarefaRequest("Implementar endpoint", null)));
        verify(tarefaRepository, never()).save(any(Tarefa.class));
    }
}