package com.exemplo.api.controller;

import com.exemplo.api.entity.Produto;
import com.exemplo.api.logger.AppLogger;
import com.exemplo.api.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService service;
    private final AppLogger logger = AppLogger.getInstance();

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        logger.debug("Recebida requisição POST /produtos.");
        Produto salvo = service.criar(produto);
        logger.info("Requisição POST concluída.");
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listar() {
        logger.debug("Recebida requisição GET /produtos.");
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        logger.debug("Recebida requisição GET /produtos/{id}.");
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id,
                                              @RequestBody Produto produto) {
        logger.debug("Recebida requisição PUT /produtos/{id}.");
        return service.atualizar(id, produto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        logger.debug("Recebida requisição DELETE /produtos/{id}.");
        if (!service.excluir(id)) {
            return ResponseEntity.notFound().build();
        }
        logger.info("Requisição DELETE concluída.");
        return ResponseEntity.noContent().build();
    }
}
