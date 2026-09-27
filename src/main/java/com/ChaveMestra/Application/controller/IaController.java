package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.dto.IaRequest;
import com.ChaveMestra.Application.dto.IaResponse;
import com.ChaveMestra.Application.model.IaInteracao;
import com.ChaveMestra.Application.service.IaInteracaoService;
import com.ChaveMestra.Application.service.IaRateLimitService;
import com.ChaveMestra.Application.service.IaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ia")
public class IaController {

    private final IaService iaService;
    private final IaInteracaoService iaInteracaoService;
    private final IaRateLimitService iaRateLimitService;

    public IaController(IaService iaService, IaInteracaoService iaInteracaoService, IaRateLimitService iaRateLimitService) {
        this.iaService = iaService;
        this.iaInteracaoService = iaInteracaoService;
        this.iaRateLimitService = iaRateLimitService;
    }

    @PostMapping("/chat")
    public ResponseEntity<IaResponse> chat(@Valid @RequestBody IaRequest request, Authentication auth) {
        iaRateLimitService.verificarLimite(auth.getName());
        return ResponseEntity.ok(iaService.chat(request));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<IaInteracao>> historico(
            @RequestParam(defaultValue = "50") int limite) {
        List<IaInteracao> interacoes = iaInteracaoService.listar().stream()
                .limit(Math.max(0, limite))
                .toList();
        return ResponseEntity.ok(interacoes);
    }

    @DeleteMapping("/historico")
    public ResponseEntity<Void> limparHistorico() {
        iaInteracaoService.excluirTodas();
        return ResponseEntity.noContent().build();
    }
}
