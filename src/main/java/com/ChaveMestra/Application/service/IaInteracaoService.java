package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import com.ChaveMestra.Application.model.IaInteracao;
import com.ChaveMestra.Application.repository.IaInteracaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

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

    @Transactional
    public void excluirTodas() {
        iaInteracaoRepository.deleteAllInBatch();
    }

    @Transactional(readOnly = true)
    public IaInteracao buscarPorId(Integer id) {
        return iaInteracaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interação de IA não encontrada"));
    }

    @Transactional
    public void atualizarResposta(Integer id, String resposta) {
        IaInteracao interacao = iaInteracaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interação de IA não encontrada"));
        interacao.setIaResposta(resposta);
        iaInteracaoRepository.save(interacao);
    }

    public IaInteracao registrar(String pergunta, String resposta, String tipo, BigDecimal tempoMs) {
        return registrar(pergunta, resposta, tipo, tempoMs, null);
    }

    public IaInteracao registrar(String pergunta, String resposta, String tipo, BigDecimal tempoMs, String modelo) {
        return registrar(pergunta, resposta, tipo, tempoMs, modelo, null);
    }

    public IaInteracao registrar(String pergunta, String resposta, String tipo, BigDecimal tempoMs,
                                 String modelo, String acaoPendente) {
        IaInteracao interacao = novaInteracao(pergunta, resposta, tipo, tempoMs, modelo);
        interacao.setAcaoPendente(acaoPendente);
        return salvar(interacao);
    }

    public IaInteracao registrar(String pergunta, String resposta, String tipo, BigDecimal tempoMs,
                                 String modelo, Integer promptTokens, Integer completionTokens, Integer totalTokens,
                                 String acaoExecutada) {
        IaInteracao interacao = novaInteracao(pergunta, resposta, tipo, tempoMs, modelo);
        interacao.setAcaoExecutada(acaoExecutada);
        return salvar(interacao);
    }

    private IaInteracao novaInteracao(String pergunta, String resposta, String tipo, BigDecimal tempoMs,
                                      String modelo) {
        IaInteracao interacao = new IaInteracao();
        interacao.setUsuarioPergunta(pergunta);
        interacao.setIaResposta(resposta);
        interacao.setTipoPergunta(tipo);
        interacao.setTempoResposta(tempoMs);
        interacao.setObservacao(modelo);
        return interacao;
    }

    private IaInteracao salvar(IaInteracao interacao) {
        try {
            return iaInteracaoRepository.save(interacao);
        } catch (RuntimeException exception) {
            log.error("Falha ao registrar interação de IA (resposta será retornada mesmo assim): ", exception);
            return interacao;
        }
    }

    @Transactional
    public String confirmarAcao(Integer id, Function<String, String> executarAcao) {
        IaInteracao interacao = iaInteracaoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interação de IA não encontrada"));
        if (interacao.getAcaoExecutada() != null) {
            throw new BusinessException("A ação desta interação já foi executada");
        }
        if (interacao.getAcaoPendente() == null || interacao.getAcaoPendente().isBlank()) {
            throw new BusinessException("Esta interação não possui uma ação pendente");
        }

        String acaoExecutada = executarAcao.apply(interacao.getAcaoPendente());
        if (acaoExecutada == null || acaoExecutada.isBlank()) {
            throw new BusinessException("A ação não retornou um identificador válido");
        }
        interacao.setAcaoExecutada(acaoExecutada);
        interacao.setAcaoPendente(null);
        iaInteracaoRepository.save(interacao);
        return acaoExecutada;
    }

    @Transactional
    public void excluir(Integer id) {
        if (!iaInteracaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Interação de IA não encontrada");
        }
        iaInteracaoRepository.deleteById(id);
    }
}
