package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.IaAvaliacaoRequest;
import com.ChaveMestra.Application.model.IaInteracao;
import com.ChaveMestra.Application.repository.IaInteracaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IaAvaliacaoServiceTest {

    @Mock
    private IaInteracaoRepository iaInteracaoRepository;

    @InjectMocks
    private IaInteracaoService iaInteracaoService;

    @Test
    void avaliarSalvaNotaComentarioEData() {
        IaInteracao interacao = new IaInteracao();
        when(iaInteracaoRepository.findById(7)).thenReturn(Optional.of(interacao));
        when(iaInteracaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IaInteracao avaliada = iaInteracaoService.avaliar(7, new IaAvaliacaoRequest(5, "Muito útil"));

        assertEquals(5, avaliada.getAvaliacao());
        assertEquals("Muito útil", avaliada.getComentarioAvaliacao());
        assertNotNull(avaliada.getAvaliadoEm());
        verify(iaInteracaoRepository).save(interacao);
    }

    @Test
    void estatisticasCalculaMediaESatisfacao() {
        List<IaInteracao> interacoes = List.of(
                interacaoComAvaliacao(5),
                interacaoComAvaliacao(4),
                interacaoComAvaliacao(3),
                interacaoComAvaliacao(1),
                new IaInteracao());
        when(iaInteracaoRepository.findAll()).thenReturn(interacoes);

        Map<String, Object> estatisticas = iaInteracaoService.estatisticasAvaliacao();

        assertEquals(5, estatisticas.get("totalInteracoes"));
        assertEquals(4, estatisticas.get("totalAvaliadas"));
        assertEquals(3.3, estatisticas.get("mediaAvaliacao"));
        assertEquals(2L, estatisticas.get("avaliacoesPositivas"));
        assertEquals(1L, estatisticas.get("avaliacoesNeutras"));
        assertEquals(1L, estatisticas.get("avaliacoesNegativas"));
        assertEquals(50L, estatisticas.get("percentualSatisfacao"));
    }

    @Test
    void estatisticasSemAvaliacoesRetornaMensagem() {
        when(iaInteracaoRepository.findAll()).thenReturn(List.of(new IaInteracao(), new IaInteracao()));

        Map<String, Object> estatisticas = iaInteracaoService.estatisticasAvaliacao();

        assertEquals(2, estatisticas.get("totalInteracoes"));
        assertEquals(0, estatisticas.get("totalAvaliadas"));
        assertEquals("Nenhuma avaliação registrada ainda", estatisticas.get("mensagem"));
    }

    private IaInteracao interacaoComAvaliacao(int avaliacao) {
        IaInteracao interacao = new IaInteracao();
        interacao.setAvaliacao(avaliacao);
        return interacao;
    }
}
