package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.AtendimentoItemResponse;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.AtendimentoItem;
import com.ChaveMestra.Application.model.Produto;
import com.ChaveMestra.Application.model.Servico;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AtendimentoItemMapper {

    public AtendimentoItemResponse toResponse(AtendimentoItem item) {
        return new AtendimentoItemResponse(
                item.getId(),
                item.getAtendimento() != null ? item.getAtendimento().getId() : null,
                item.getProduto() != null ? item.getProduto().getId() : null,
                item.getProduto() != null ? item.getProduto().getNome() : null,
                item.getServico() != null ? item.getServico().getId() : null,
                item.getServico() != null ? item.getServico().getNome() : null,
                item.getQuantidade(),
                item.getValorUnitario(),
                item.getValorTotal(),
                item.getDesconto(),
                item.getTipo(),
                item.getObservacao()
        );
    }

    /**
     * Cria a entidade a partir do request + objetos já resolvidos pelo Service.
     * O valorTotal é calculado aqui: (quantidade * valorUnitario) - desconto.
     */
    public AtendimentoItem toEntity(Atendimento atendimento,
                                    Produto produto,
                                    Servico servico,
                                    Integer quantidade,
                                    BigDecimal valorUnitario,
                                    BigDecimal desconto,
                                    String tipo,
                                    String observacao) {

        BigDecimal descontoFinal = desconto != null ? desconto : BigDecimal.ZERO;

        BigDecimal valorTotal = valorUnitario
                .multiply(BigDecimal.valueOf(quantidade))
                .subtract(descontoFinal);

        AtendimentoItem item = new AtendimentoItem();
        item.setAtendimento(atendimento);
        item.setProduto(produto);
        item.setServico(servico);
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        item.setDesconto(descontoFinal);
        item.setValorTotal(valorTotal);
        item.setTipo(tipo);
        item.setObservacao(observacao);
        return item;
    }
}