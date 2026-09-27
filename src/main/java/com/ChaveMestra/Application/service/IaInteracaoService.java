package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import com.ChaveMestra.Application.model.IaInteracao;
import com.ChaveMestra.Application.repository.IaInteracaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class IaInteracaoService {

    private static final Logger log = LoggerFactory.getLogger(IaInteracaoService.class);

    private final IaInteracaoRepository iaInteracaoRepository;

    public IaInteracaoService(IaInteracaoRepository iaInteracaoRepository) {
        this.iaInteracaoRepository = iaInteracaoRepository;
    }

    @Transactional(readOnly = true)
    public List<IaInteracao> listar() {
        return iaInteracaoRepository.findAllByOrderByDataInteracaoDesc();
    }

    @Transactional(readOnly = true)
    public IaInteracao buscarPorId(Integer id) {
        return iaInteracaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interação de IA não encontrada"));
    }

    public IaInteracao registrar(String pergunta, String resposta, String tipo, BigDecimal tempoMs) {
        return registrar(pergunta, resposta, tipo, tempoMs, null);
    }

    public IaInteracao registrar(String pergunta, String resposta, String tipo, BigDecimal tempoMs, String modelo) {
        IaInteracao interacao = new IaInteracao();
        interacao.setUsuarioPergunta(pergunta);
        interacao.setIaResposta(resposta);
        interacao.setTipoPergunta(tipo);
        interacao.setTempoResposta(tempoMs);
        interacao.setObservacao(modelo);
        try {
            return iaInteracaoRepository.save(interacao);
        } catch (RuntimeException exception) {
            log.error("Falha ao registrar interação de IA (resposta será retornada mesmo assim): ", exception);
            return interacao;
        }
    }

    @Transactional
    public void excluir(Integer id) {
        if (!iaInteracaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Interação de IA não encontrada");
        }
        iaInteracaoRepository.deleteById(id);
    }
}
