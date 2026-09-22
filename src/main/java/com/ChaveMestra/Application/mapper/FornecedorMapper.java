package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.FornecedorRequest;
import com.ChaveMestra.Application.dto.FornecedorResponse;
import com.ChaveMestra.Application.model.Fornecedor;
import org.springframework.stereotype.Component;

@Component
public class FornecedorMapper {

    //
    public FornecedorResponse toResponse(Fornecedor fornecedor){
         return new FornecedorResponse(
                        fornecedor.getId(),
                        fornecedor.getNome(),
                        fornecedor.getCnpj(),
                        fornecedor.getTelefone()
                );
    }

    public Fornecedor toFornecedor(FornecedorRequest request){
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setNome(request.nome());
            fornecedor.setCnpj(request.cnpj());
            fornecedor.setTelefone(request.telefone());

            return fornecedor;
        }
    }



