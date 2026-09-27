package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.config.SecurityConfig;
import com.ChaveMestra.Application.service.RelatorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({RelatorioController.class, SecurityConfig.class, TestJsonConfiguration.class})
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RelatorioService relatorioService;

    @Test
    @WithMockUser
    void produtosBaixoEstoqueRetornaOk() throws Exception {
        when(relatorioService.produtosBaixoEstoque(10))
                .thenReturn(new RelatorioService.ProdutosBaixoEstoqueRelatorio(0, List.of()));

        mockMvc.perform(get("/relatorios/produtos-baixo-estoque"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void clientesPorEstadoRetornaOk() throws Exception {
        when(relatorioService.clientesPorEstado()).thenReturn(List.of());

        mockMvc.perform(get("/relatorios/clientes-por-estado"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void financeiroResumoRetornaOk() throws Exception {
        when(relatorioService.financeiroResumo()).thenReturn(new RelatorioService.FinanceiroResumo(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "2026-09"));

        mockMvc.perform(get("/relatorios/financeiro-resumo"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void estoqueCriticoRetornaOk() throws Exception {
        when(relatorioService.estoqueCritico()).thenReturn(List.of());

        mockMvc.perform(get("/relatorios/estoque-critico"))
                .andExpect(status().isOk());
    }
}
