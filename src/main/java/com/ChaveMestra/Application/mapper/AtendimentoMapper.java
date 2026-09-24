package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.AtendimentoRequest;
import com.ChaveMestra.Application.dto.AtendimentoResponse;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.Cliente;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class AtendimentoMapper {

    public AtendimentoResponse toResponse(Atendimento atendimento) {
        return new AtendimentoResponse(
                atendimento.getId(),
                atendimento.getCliente() != null ? atendimento.getCliente().getId() : null,
                atendimento.getCliente() != null ? atendimento.getCliente().getNome() : null,
                atendimento.getData(),
                atendimento.getFormaPagamento(),
                atendimento.getValorTotal(),
                atendimento.getDesconto(),
                atendimento.getValorLiquido(),
                atendimento.getStatus(),
                atendimento.getObservacao()
        );
    }

    public Atendimento toEntity(AtendimentoRequest request, Cliente cliente) {
        Atendimento atendimento = new Atendimento();
        atendimento.setCliente(cliente);
        atendimento.setData(LocalDateTime.now());
        atendimento.setFormaPagamento(request.formaPagamento());
        atendimento.setValorTotal(BigDecimal.ZERO); // Será calculado após adicionar itens
        atendimento.setDesconto(request.desconto() != null ? request.desconto() : BigDecimal.ZERO);
        atendimento.setStatus("aberto");
        atendimento.setObservacao(request.observacao());
        return atendimento;
    }
}