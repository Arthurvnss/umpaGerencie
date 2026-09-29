package br.com.senac.atividades.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AtividadeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarAtividadeEDevolverJsonComStatus201() throws Exception {
        mockMvc.perform(post("/api/atividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Preparar apresentação",
                                  "descricao": "Revisar os slides"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("Preparar apresentação"))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.dataCriacao").isNotEmpty());
    }

    @Test
    void deveRejeitarTituloCurtoComStatus400() throws Exception {
        mockMvc.perform(post("/api/atividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "API"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.mensagem").isNotEmpty());
    }

    @Test
    void deveRetornar404AoBuscarAtividadeInexistente() throws Exception {
        mockMvc.perform(get("/api/atividades/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }
}