package com.exemplo.api.cep;

import com.exemplo.api.cep.adapter.CepNaoEncontradoException;
import com.exemplo.api.cep.proxy.CepInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cep")
public class CepController {
    private final ConsultaCep consultaCep;

    public CepController(ConsultaCep consultaCep) {
        this.consultaCep = consultaCep;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<Endereco> buscar(@PathVariable String cep) {
        return ResponseEntity.ok(consultaCep.buscar(cep));
    }

    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<Map<String, String>> cepInvalido(CepInvalidoException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> cepNaoEncontrado(CepNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
    }
}
