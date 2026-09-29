package br.com.senac.atividades.service;

import br.com.senac.atividades.dto.AtualizarAtividadesRequest;
import br.com.senac.atividades.dto.CriarAtividadesRequest;
import br.com.senac.atividades.dto.AtividadesResponse;
import br.com.senac.atividades.exception.RecursoNaoEncontradoException;
import br.com.senac.atividades.exception.RegraDeNegocioException;
import br.com.senac.atividades.model.StatusAtividades;
import br.com.senac.atividades.model.Atividades;
import br.com.senac.atividades.repository.AtividadesRepository;
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
               Atividade atividade = new Atividade("Consultar atividade", "Detalhes", StatusAtividade.PENDENTE);
                when(atividadeRepository.findById(42L)).thenReturn(Optional.of(atividade));

                AtividadeResponse resposta = atividadeService.buscarPorId(42L);

                assertEquals("Consultar atividade", resposta.titulo());
                assertEquals(StatusAtividade.PENDENTE, resposta.status());
                verify(atividadeRepository).findById(42L);
        }

    @Test
    void deveAtualizarAtividadeERegistrarDataConclusao() {
        Atividade atividade = new Atividade("Revisar documentação", null, StatusAtividade.EM_ANDAMENTO);
        when(atividadeRepository.findById(7L)).thenReturn(Optional.of(atividade));
        when(atividadeRepository.save(any(Atividade.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        AtividadeResponse resposta = atividadeService.atualizar(7L,
                new AtualizarAtividadeRequest("Revisar documentação final", "Revisão concluída",
                        StatusAtividade.CONCLUIDA));

        assertEquals(StatusAtividade.CONCLUIDA, resposta.status());
        assertNotNull(resposta.dataConclusao());
        verify(atividadeRepository).save(atividade);
    }

    @Test
    void naoDevePermitirConcluirAtividadePendenteDiretamente() {
        Atividade atividade = new Atividade("Revisar documentação", null, StatusAtividade.PENDENTE);
        when(atividadeRepository.findById(7L)).thenReturn(Optional.of(atividade));

        assertThrows(RegraDeNegocioException.class,
                () -> atividadeService.atualizar(7L,
                        new AtualizarTarefaRequest("Revisar documentação", null,
                                StatusAtividade.CONCLUIDA)));
        verify(atividadeRepository, never()).save(any(Atividade.class));
    }

    @Test
    void naoDeveExcluirAtividadeConcluida() {
        Atividade atividade = new Atividade("Revisar documentação", null, StatusAtividade.CONCLUIDA);
        when(atividadeRepository.findById(7L)).thenReturn(Optional.of(atividade));

        assertThrows(RegraDeNegocioException.class, () -> atividadeService.excluir(7L));
        verify(atividadeRepository, never()).delete(any(Atividade.class));
    }

    @Test
    void naoDeveCriarTituloDuplicadoAtivo() {
        when(atividadeRepository.existsByTituloIgnoreCaseAndStatusNot(
                "Implementar endpoint", StatusAtividade.CONCLUIDA)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class,
                () -> atividadeService.criar(new CriarAtividadeRequest("Implementar endpoint", null)));
        verify(atividadeRepository, never()).save(any(Atividade.class));
    }
}