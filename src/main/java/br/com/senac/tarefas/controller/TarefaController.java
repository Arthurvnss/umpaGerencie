package br.com.senac.atividades.controller;

import br.com.senac.atividades.dto.AtualizarAtividadesRequest;
import br.com.senac.atividades.dto.CriarAtividadesRequest;
import br.com.senac.atividades.dto.AtividadesResponse;
import br.com.senac.atividades.service.AtividadesService;
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
@RequestMapping("/api/atividades")
public class AtividadesController {

    private final AtividadesService atividadesService;

    public AtividadesController(AtividadesService atividadesService) {
        this.atividadesService = atividadesService;
    }

    @PostMapping
    public ResponseEntity<AtividadesResponse> criar(@Valid @RequestBody CriarAtividadesRequest request) {
        AtividadesResponse atividades = atividadesService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(atividades.id())
                .toUri();
        return ResponseEntity.created(location).body(atividades);
    }

    @GetMapping
    public List<AtividadesResponse> listar() {
        return atividadesService.listar();
    }

    @GetMapping("/{id}")
    public AtividadesResponse buscarPorId(@PathVariable Long id) {
        return atividadesService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AtividadesResponse atualizar(
            @PathVariable Long id, @Valid @RequestBody AtualizarAtividadesRequest request) {
        return atividadesService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        atividadesService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}