package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.config.SecurityConfig;
import com.ChaveMestra.Application.dto.IaResponse;
import com.ChaveMestra.Application.model.IaInteracao;
import com.ChaveMestra.Application.service.IaInteracaoService;
import com.ChaveMestra.Application.service.IaRateLimitService;
import com.ChaveMestra.Application.service.IaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({IaController.class, SecurityConfig.class, TestJsonConfiguration.class})
class IaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IaService iaService;

    @MockitoBean
    private IaInteracaoService iaInteracaoService;

    @MockitoBean
    private IaRateLimitService iaRateLimitService;

    @Test
    @WithMockUser
    void chatComPerguntaValidaRetornaInteracao() throws Exception {
        when(iaService.chat(any())).thenReturn(new IaResponse(
                1, "pergunta", "resposta mock", "deepseek/deepseek-chat",
                BigDecimal.TEN, LocalDateTime.now()));

        mockMvc.perform(post("/ia/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pergunta\":\"Quantos clientes temos?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interacaoId").value(1));
    }

    @Test
    @WithMockUser
    void chatComPerguntaVaziaRetornaBadRequest() throws Exception {
        mockMvc.perform(post("/ia/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pergunta\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void chatComPerguntaMaiorQueMilCaracteresRetornaBadRequest() throws Exception {
        String pergunta = "a".repeat(1001);

        mockMvc.perform(post("/ia/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pergunta\":\"" + pergunta + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void chatSemAutenticacaoRetornaUnauthorized() throws Exception {
        mockMvc.perform(post("/ia/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pergunta\":\"pergunta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void historicoRetornaLista() throws Exception {
        IaInteracao interacao = new IaInteracao();
        interacao.setId(1);
        interacao.setUsuarioPergunta("pergunta");
        when(iaInteracaoService.listar()).thenReturn(List.of(interacao));

        mockMvc.perform(get("/ia/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void historicoComLimiteRetornaNoMaximoCincoItens() throws Exception {
        List<IaInteracao> interacoes = java.util.stream.IntStream.rangeClosed(1, 7)
                .mapToObj(id -> {
                    IaInteracao interacao = new IaInteracao();
                    interacao.setId(id);
                    interacao.setUsuarioPergunta("pergunta " + id);
                    return interacao;
                })
                .toList();
        when(iaInteracaoService.listar()).thenReturn(interacoes);

        mockMvc.perform(get("/ia/historico").param("limite", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5));
    }
}

@Configuration(proxyBeanMethods = false)
@EnableWebMvc
class TestJsonConfiguration implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder converters) {
        converters.withJsonConverter(new JacksonJsonHttpMessageConverter());
    }
}
