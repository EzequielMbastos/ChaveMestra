package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.ClienteRequest;
import com.ChaveMestra.Application.dto.ClienteResponse;
import com.ChaveMestra.Application.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteResponse toResponse(Cliente cliente){
        ClienteResponse clienteResponse =
                new ClienteResponse(
                        cliente.getId(),
                        cliente.getNome(),
                        cliente.getCpf(),
                        cliente.getTelefone(),
                        cliente.getEndereco()
                );
        return clienteResponse;
    }

    public Cliente toCliente(ClienteRequest request){
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());
        cliente.setEndereco(request.endereco());
        cliente.setTelefone(request.telefone());

        return cliente;
    }
}


