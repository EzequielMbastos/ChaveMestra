package com.ChaveMestra.Apllicattion.mapper;

import com.ChaveMestra.Apllicattion.dto.FornecedorRequest;
import com.ChaveMestra.Apllicattion.dto.FornecedorResponse;
import com.ChaveMestra.Apllicattion.model.Fornecedor;
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



