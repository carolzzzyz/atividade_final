package com.exemplo.api.comparacao;

import com.exemplo.api.entity.Produto;
import com.exemplo.api.logger.AppLoggerSpring;
import com.exemplo.api.repository.ProdutoRepository;

import java.util.List;
import java.util.Optional;

/*
 * Exemplo de comparação: mostra a injeção do logger gerenciado pelo Spring.
 * Esta classe não é anotada com @Service porque a versão final utiliza
 * o Singleton clássico em ProdutoService.
 */
public class ProdutoServiceComSpringLogger {
    private final ProdutoRepository repository;
    private final AppLoggerSpring logger;

    public ProdutoServiceComSpringLogger(
            ProdutoRepository repository,
            AppLoggerSpring logger) {
        this.repository = repository;
        this.logger = logger;
    }

    public List<Produto> listar() {
        logger.debug("Listando produtos usando o logger do Spring.");
        List<Produto> produtos = repository.findAll();
        logger.info("Listagem concluída. Quantidade: " + produtos.size());
        return produtos;
    }

    public Optional<Produto> buscarPorId(Long id) {
        logger.debug("Buscando produto usando o logger do Spring. ID: " + id);
        Optional<Produto> produto = repository.findById(id);
        if (produto.isPresent()) {
            logger.info("Produto encontrado. ID: " + id);
        } else {
            logger.erro("Produto não encontrado. ID: " + id);
        }
        return produto;
    }
}
