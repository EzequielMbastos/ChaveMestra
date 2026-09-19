package com.ChaveMestra.Apllicattion.mapper;


import com.ChaveMestra.Apllicattion.dto.ServicoRequest;
import com.ChaveMestra.Apllicattion.dto.ServicoResponse;
import com.ChaveMestra.Apllicattion.model.Servico;
import org.springframework.stereotype.Component;

@Component
public class ServicoMapper {

    public ServicoResponse toResponse(Servico servico){
        return new ServicoResponse(servico.getId(), servico.getNome(),
                servico.getDescricao(), servico.getPrecoBase());
    }

    public Servico toServico(ServicoRequest request){
        Servico servico = new Servico();
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        servico.setPrecoBase(request.precoBase());

        return servico;
    }
}
