package br.com.senac.atividades.repository;

import br.com.senac.atividades.model.StatusAtividades;
import br.com.senac.atividades.model.Atividades;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    boolean existsByTituloIgnoreCaseAndStatusNot(String titulo, StatusAtividade status);

    boolean existsByTituloIgnoreCaseAndStatusNotAndIdNot(
            String titulo, StatusAtividade status, Long id);
}