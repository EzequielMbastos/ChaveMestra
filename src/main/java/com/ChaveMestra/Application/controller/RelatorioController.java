package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping
    public ResponseEntity<RelatorioService.PeriodoFinanceiroRelatorio> relatorioFinanceiro(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(relatorioService.relatorioFinanceiro(inicio, fim));
    }

    @GetMapping("/produtos-baixo-estoque")
    public ResponseEntity<RelatorioService.ProdutosBaixoEstoqueRelatorio> produtosBaixoEstoque(
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(relatorioService.produtosBaixoEstoque(limite));
    }

    @GetMapping("/clientes-por-estado")
    public ResponseEntity<List<RelatorioService.ClientesPorEstado>> clientesPorEstado() {
        return ResponseEntity.ok(relatorioService.clientesPorEstado());
    }

    @GetMapping("/financeiro-resumo")
    public ResponseEntity<RelatorioService.FinanceiroResumo> financeiroResumo() {
        return ResponseEntity.ok(relatorioService.financeiroResumo());
    }

    @GetMapping("/atendimentos-recentes")
    public ResponseEntity<List<RelatorioService.AtendimentoRecente>> atendimentosRecentes(
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(relatorioService.atendimentosRecentes(limite));
    }

    @GetMapping("/estoque-critico")
    public ResponseEntity<List<RelatorioService.EstoqueCritico>> estoqueCritico() {
        return ResponseEntity.ok(relatorioService.estoqueCritico());
    }
}
