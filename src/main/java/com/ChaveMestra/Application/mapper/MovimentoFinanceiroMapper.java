package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.MovimentoFinanceiroRequest;
import com.ChaveMestra.Application.dto.MovimentoFinanceiroResponse;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.CategoriaFinanceira;
import com.ChaveMestra.Application.model.MovimentoFinanceiro;
import org.springframework.stereotype.Component;

@Component
public class MovimentoFinanceiroMapper {

    public MovimentoFinanceiroResponse toResponse(MovimentoFinanceiro movimento) {
        return new MovimentoFinanceiroResponse(
                movimento.getId(),
                movimento.getCategoria() != null ? movimento.getCategoria().getId() : null,
                movimento.getCategoria() != null ? movimento.getCategoria().getNome() : null,
                movimento.getAtendimento() != null ? movimento.getAtendimento().getId() : null,
                movimento.getNome(),
                movimento.getDescricao(),
                movimento.getValor(),
                movimento.getDataMovimento(),
                movimento.getVencimento(),
                movimento.getStatus()
        );
    }

    public MovimentoFinanceiro toEntity(MovimentoFinanceiroRequest request,
                                        CategoriaFinanceira categoria,
                                        Atendimento atendimento) {
        MovimentoFinanceiro movimento = new MovimentoFinanceiro();
        movimento.setCategoria(categoria);
        movimento.setAtendimento(atendimento);
        movimento.setNome(request.nome());
        movimento.setDescricao(request.descricao());
        movimento.setValor(request.valor());
        movimento.setDataMovimento(request.dataMovimento());
        movimento.setVencimento(request.vencimento());
        movimento.setStatus(request.status() != null ? request.status() : "pendente");
        return movimento;
    }
}