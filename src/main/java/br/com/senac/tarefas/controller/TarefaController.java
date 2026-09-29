package br.com.senac.tarefas.controller;

import br.com.senac.tarefas.dto.AtualizarTarefaRequest;
import br.com.senac.tarefas.dto.CriarTarefaRequest;
import br.com.senac.tarefas.dto.TarefaResponse;
import br.com.senac.tarefas.service.TarefaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public ResponseEntity<TarefaResponse> criar(@Valid @RequestBody CriarTarefaRequest request) {
        TarefaResponse tarefa = tarefaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tarefa.id())
                .toUri();
        return ResponseEntity.created(location).body(tarefa);
    }

    @GetMapping
    public List<TarefaResponse> listar() {
        return tarefaService.listar();
    }

    @GetMapping("/{id}")
    public TarefaResponse buscarPorId(@PathVariable Long id) {
        return tarefaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TarefaResponse atualizar(
            @PathVariable Long id, @Valid @RequestBody AtualizarTarefaRequest request) {
        return tarefaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        tarefaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}