package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.AtendimentoRequest;
import com.ChaveMestra.Application.dto.AtendimentoResponse;
import com.ChaveMestra.Application.mapper.AtendimentoMapper;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.Cliente;
import com.ChaveMestra.Application.repository.AtendimentoRepository;
import com.ChaveMestra.Application.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final ClienteRepository clienteRepository;
    private final AtendimentoMapper atendimentoMapper;

    public AtendimentoService(AtendimentoRepository atendimentoRepository,
                              ClienteRepository clienteRepository,
                              AtendimentoMapper atendimentoMapper) {
        this.atendimentoRepository = atendimentoRepository;
        this.clienteRepository = clienteRepository;
        this.atendimentoMapper = atendimentoMapper;
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponse> listar() {
        return atendimentoRepository.findAll()
                .stream()
                .map(atendimentoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AtendimentoResponse buscarPorId(Integer id) {
        Atendimento atendimento = atendimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));
        return atendimentoMapper.toResponse(atendimento);
    }

    @Transactional
    public AtendimentoResponse cadastrar(AtendimentoRequest request) {
        Cliente cliente = null;
        if (request.clienteId() != null) {
            cliente = clienteRepository.findById(request.clienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        }

        Atendimento atendimento = atendimentoMapper.toEntity(request, cliente);
        Atendimento salvo = atendimentoRepository.save(atendimento);
        return atendimentoMapper.toResponse(salvo);
    }

    @Transactional
    public AtendimentoResponse atualizar(Integer id, AtendimentoRequest request) {
        Atendimento atendimento = atendimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        Cliente cliente = null;
        if (request.clienteId() != null) {
            cliente = clienteRepository.findById(request.clienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        }

        atendimento.setCliente(cliente);
        atendimento.setFormaPagamento(request.formaPagamento());
        atendimento.setDesconto(request.desconto());
        atendimento.setObservacao(request.observacao());

        Atendimento atualizado = atendimentoRepository.save(atendimento);
        return atendimentoMapper.toResponse(atualizado);
    }

    @Transactional
    public void excluir(Integer id) {
        if (!atendimentoRepository.existsById(id)) {
            throw new RuntimeException("Atendimento não encontrado");
        }
        atendimentoRepository.deleteById(id);
    }
}