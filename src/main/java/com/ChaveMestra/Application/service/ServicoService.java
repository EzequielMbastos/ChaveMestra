package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.ServicoRequest;
import com.ChaveMestra.Application.dto.ServicoResponse;
import com.ChaveMestra.Application.mapper.ServicoMapper;
import com.ChaveMestra.Application.model.Servico;
import com.ChaveMestra.Application.repository.AtendimentoItemRepository;
import com.ChaveMestra.Application.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;
    private final AtendimentoItemRepository atendimentoItemRepository;

    public ServicoService(ServicoRepository servicoRepository,
                             ServicoMapper servicoMapper,
                             AtendimentoItemRepository atendimentoItemRepository) {
        this.servicoRepository = servicoRepository;
        this.servicoMapper = servicoMapper;
        this.atendimentoItemRepository = atendimentoItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listar() {
        List<ServicoResponse> listServicoResponse = servicoRepository.findAll().stream().map(servico -> servicoMapper.toResponse(servico)).collect(Collectors.toList());
        return listServicoResponse;
    }

    @Transactional
    public ServicoResponse cadastrar(ServicoRequest request) {
        Servico servico = servicoMapper.toServico(request);
        Servico servicoSalvo = servicoRepository.save(servico);
        return servicoMapper.toResponse(servicoSalvo);
    }

    @Transactional
    public void excluir(Integer id) {
        if (atendimentoItemRepository.existsByServicoId(id)) {
            throw new RuntimeException("Nao foi possivel excluir");
        }
        servicoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ServicoResponse buscarPorId(Integer id) {
        Optional<Servico> objetoBuscado = servicoRepository.findById(id);
        Servico servico = objetoBuscado.orElseThrow(() -> new RuntimeException("Servico não encontrado"));
        return servicoMapper.toResponse(servico);
    }

    @Transactional
    public ServicoResponse atualizar(Integer id, ServicoRequest dados) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servico nao existente"));
        servico.setNome(dados.nome());
        servico.setDescricao(dados.descricao());
        servico.setPrecoBase(dados.precoBase());
        servico = servicoRepository.save(servico);
        return servicoMapper.toResponse(servico);
    }
}
