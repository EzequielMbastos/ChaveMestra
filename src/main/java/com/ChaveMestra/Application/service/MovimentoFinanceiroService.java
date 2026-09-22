package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.MovimentoFinanceiroRequest;
import com.ChaveMestra.Application.dto.MovimentoFinanceiroResponse;
import com.ChaveMestra.Application.mapper.MovimentoFinanceiroMapper;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.CategoriaFinanceira;
import com.ChaveMestra.Application.model.MovimentoFinanceiro;
import com.ChaveMestra.Application.repository.AtendimentoRepository;
import com.ChaveMestra.Application.repository.CategoriaFinanceiraRepository;
import com.ChaveMestra.Application.repository.MovimentoFinanceiroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentoFinanceiroService {

    private final MovimentoFinanceiroRepository movimentoRepository;
    private final CategoriaFinanceiraRepository categoriaRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final MovimentoFinanceiroMapper movimentoMapper;

    public MovimentoFinanceiroService(MovimentoFinanceiroRepository movimentoRepository,
                                      CategoriaFinanceiraRepository categoriaRepository,
                                      AtendimentoRepository atendimentoRepository,
                                      MovimentoFinanceiroMapper movimentoMapper) {
        this.movimentoRepository = movimentoRepository;
        this.categoriaRepository = categoriaRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.movimentoMapper = movimentoMapper;
    }

    @Transactional(readOnly = true)
    public List<MovimentoFinanceiroResponse> listar() {
        return movimentoRepository.findAll()
                .stream()
                .map(movimentoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimentoFinanceiroResponse buscarPorId(Integer id) {
        MovimentoFinanceiro movimento = movimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimento financeiro não encontrado"));
        return movimentoMapper.toResponse(movimento);
    }

    @Transactional
    public MovimentoFinanceiroResponse cadastrar(MovimentoFinanceiroRequest request) {
        CategoriaFinanceira categoria = categoriaRepository.findById(request.categoriaFinanceiraId())
                .orElseThrow(() -> new RuntimeException("Categoria financeira não encontrada"));

        Atendimento atendimento = null;
        if (request.atendimentoId() != null) {
            atendimento = atendimentoRepository.findById(request.atendimentoId())
                    .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));
        }

        MovimentoFinanceiro movimento = movimentoMapper.toEntity(request, categoria, atendimento);

        // Se dataMovimento não foi informada, usa agora
        if (movimento.getDataMovimento() == null) {
            movimento.setDataMovimento(LocalDateTime.now());
        }

        MovimentoFinanceiro salvo = movimentoRepository.save(movimento);
        return movimentoMapper.toResponse(salvo);
    }

    @Transactional
    public MovimentoFinanceiroResponse atualizar(Integer id, MovimentoFinanceiroRequest request) {
        MovimentoFinanceiro movimento = movimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimento financeiro não encontrado"));

        CategoriaFinanceira categoria = categoriaRepository.findById(request.categoriaFinanceiraId())
                .orElseThrow(() -> new RuntimeException("Categoria financeira não encontrada"));

        Atendimento atendimento = null;
        if (request.atendimentoId() != null) {
            atendimento = atendimentoRepository.findById(request.atendimentoId())
                    .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));
        }

        movimento.setCategoria(categoria);
        movimento.setAtendimento(atendimento);
        movimento.setNome(request.nome());
        movimento.setDescricao(request.descricao());
        movimento.setValor(request.valor());
        movimento.setDataMovimento(request.dataMovimento() != null
                ? request.dataMovimento()
                : movimento.getDataMovimento());
        movimento.setVencimento(request.vencimento());
        if (request.status() != null) {
            movimento.setStatus(request.status());
        }

        MovimentoFinanceiro atualizado = movimentoRepository.save(movimento);
        return movimentoMapper.toResponse(atualizado);
    }

    @Transactional
    public void excluir(Integer id) {
        if (!movimentoRepository.existsById(id)) {
            throw new RuntimeException("Movimento financeiro não encontrado");
        }
        movimentoRepository.deleteById(id);
    }
}