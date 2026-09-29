package br.com.senac.tarefas.repository;

import br.com.senac.tarefas.model.StatusTarefa;
import br.com.senac.tarefas.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    boolean existsByTituloIgnoreCaseAndStatusNot(String titulo, StatusAtividade status);

    boolean existsByTituloIgnoreCaseAndStatusNotAndIdNot(
            String titulo, StatusAtividade status, Long id);
}