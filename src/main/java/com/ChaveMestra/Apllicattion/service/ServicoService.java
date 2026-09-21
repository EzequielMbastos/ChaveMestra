package com.ChaveMestra.Apllicattion.service;

import com.ChaveMestra.Apllicattion.dto.ServicoRequest;
import com.ChaveMestra.Apllicattion.dto.ServicoResponse;
import com.ChaveMestra.Apllicattion.mapper.ServicoMapper;
import com.ChaveMestra.Apllicattion.model.Servico;
import com.ChaveMestra.Apllicattion.repository.AtendimentoItemRepository;
import com.ChaveMestra.Apllicattion.repository.ServicoRepository;
import org.springframework.stereotype.Service;


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

    public List<ServicoResponse> listar() {
        List<ServicoResponse> listServicoResponse = servicoRepository.findAll().stream().map(servico -> servicoMapper.toResponse(servico)).collect(Collectors.toList());
        return listServicoResponse;
    }

    public ServicoResponse cadastrar(ServicoRequest request) {
        Servico servico = servicoMapper.toServico(request);
        Servico servicoSalvo = servicoRepository.save(servico);
        return servicoMapper.toResponse(servicoSalvo);
    }

    public void excluir(Integer id) {
        if (atendimentoItemRepository.existsByServicoId(id)) {
            throw new RuntimeException("Nao foi possivel excluir");
        }
        servicoRepository.deleteById(id);
    }

    public ServicoResponse buscarPorId(Integer id) {
        Optional<Servico> objetoBuscado = servicoRepository.findById(id);
        Servico servico = objetoBuscado.orElseThrow(() -> new RuntimeException("Servico não encontrado"));
        return servicoMapper.toResponse(servico);
    }

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
