package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.ClienteRequest;
import com.ChaveMestra.Application.dto.ClienteResponse;
import com.ChaveMestra.Application.mapper.ClienteMapper;
import com.ChaveMestra.Application.model.Cliente;
import com.ChaveMestra.Application.repository.AtendimentoRepository;
import com.ChaveMestra.Application.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final AtendimentoRepository atendimentoRepository;

    public ClienteService(ClienteRepository clienteRepository,
                           ClienteMapper clienteMapper,
                           AtendimentoRepository atendimentoRepository) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.atendimentoRepository = atendimentoRepository;
    }

    public List<ClienteResponse> listar() {
        List<ClienteResponse> listClienteResponse = clienteRepository.findAll().stream().map(cliente -> clienteMapper.toResponse(cliente)).collect(Collectors.toList());
        return listClienteResponse;
    }

    public ClienteResponse cadastrar(ClienteRequest request) {
        Cliente cliente = clienteMapper.toCliente(request);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return clienteMapper.toResponse(clienteSalvo);
    }

    public void excluir(Integer id) {
        if (atendimentoRepository.existsByClienteId(id)) {
            throw new RuntimeException("Nao foi possivel excluir");
        }
        clienteRepository.deleteById(id);
    }

    public ClienteResponse buscarPorId(Integer id) {
        Optional<Cliente> objetoBuscado = clienteRepository.findById(id);
        Cliente cliente = objetoBuscado.orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return clienteMapper.toResponse(cliente);
    }

    public ClienteResponse atualizar(Integer id, ClienteRequest dados) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente nao existente"));
        cliente.setNome(dados.nome());
        cliente.setCpf(dados.cpf());
        cliente.setTelefone(dados.telefone());
        cliente.setEndereco(dados.endereco());
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toResponse(cliente);
    }






}
