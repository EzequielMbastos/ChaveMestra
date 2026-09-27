package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import com.ChaveMestra.Application.model.IaInteracao;
import com.ChaveMestra.Application.repository.IaInteracaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IaInteracaoServiceTest {

    @Mock
    private IaInteracaoRepository iaInteracaoRepository;

    @InjectMocks
    private IaInteracaoService iaInteracaoService;

    @Test
    void registrarCriaEntidadeComCamposCorretos() {
        when(iaInteracaoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        iaInteracaoService.registrar("pergunta", "resposta", "consulta", BigDecimal.TEN, "modelo");

        ArgumentCaptor<IaInteracao> captor = ArgumentCaptor.forClass(IaInteracao.class);
        verify(iaInteracaoRepository).save(captor.capture());
        IaInteracao registrada = captor.getValue();
        assertEquals("pergunta", registrada.getUsuarioPergunta());
        assertEquals("resposta", registrada.getIaResposta());
        assertEquals("consulta", registrada.getTipoPergunta());
        assertEquals(BigDecimal.TEN, registrada.getTempoResposta());
        assertEquals("modelo", registrada.getObservacao());
    }

    @Test
    void listarRetornaListaDoRepository() {
        List<IaInteracao> interacoes = List.of(new IaInteracao());
        when(iaInteracaoRepository.findAllByOrderByDataInteracaoDesc()).thenReturn(interacoes);

        assertSame(interacoes, iaInteracaoService.listar());
    }

    @Test
    void buscarPorIdExistenteRetornaInteracao() {
        IaInteracao interacao = new IaInteracao();
        when(iaInteracaoRepository.findById(1)).thenReturn(Optional.of(interacao));

        assertSame(interacao, iaInteracaoService.buscarPorId(1));
    }

    @Test
    void buscarPorIdInexistenteLancaResourceNotFoundException() {
        when(iaInteracaoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> iaInteracaoService.buscarPorId(1));
    }

    @Test
    void excluirIdExistenteChamaDeleteById() {
        when(iaInteracaoRepository.existsById(1)).thenReturn(true);

        iaInteracaoService.excluir(1);

        verify(iaInteracaoRepository).deleteById(1);
    }

    @Test
    void excluirIdInexistenteLancaResourceNotFoundException() {
        when(iaInteracaoRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> iaInteracaoService.excluir(1));
    }
}
