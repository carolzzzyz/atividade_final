package com.exemplo.api.controller;

import com.exemplo.api.logger.AppLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final AppLogger logger = AppLogger.getInstance();

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> regra(org.springframework.web.server.ResponseStatusException e) {
        logger.erro(e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("erro", e.getReason()));
    }
    @ExceptionHandler({com.exemplo.api.cep.proxy.CepInvalidoException.class, org.springframework.http.converter.HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String,String>> invalido(Exception e) {
        logger.erro("Dados inválidos: " + e.getClass().getSimpleName());
        return ResponseEntity.badRequest().body(Map.of("erro", "Dados ou CEP inválidos"));
    }
    @ExceptionHandler(com.exemplo.api.cep.adapter.CepNaoEncontradoException.class)
    public ResponseEntity<Map<String,String>> cepAusente(Exception e) {
        logger.erro("CEP não encontrado");
        return ResponseEntity.status(404).body(Map.of("erro", "CEP não encontrado"));
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String>> integridade(Exception e) {
        logger.erro("Conflito de integridade");
        return ResponseEntity.status(409).body(Map.of("erro", "Operação incompatível com registros vinculados"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarExcecao(Exception e) {
        logger.erro("Exceção não tratada: " + e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Ocorreu um erro interno no servidor."));
    }
}
