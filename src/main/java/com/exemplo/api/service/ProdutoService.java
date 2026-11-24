package com.exemplo.api.service;

import com.exemplo.api.entity.Produto;
import com.exemplo.api.logger.AppLogger;
import com.exemplo.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository repository;
    private final AppLogger logger = AppLogger.getInstance();

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto criar(Produto produto) {
        try {
            logger.debug("Iniciando cadastro de produto.");
            Produto salvo = repository.save(produto);
            logger.info("Cadastro de produto concluído. ID: " + salvo.getId());
            return salvo;
        } catch (RuntimeException e) {
            logger.erro("Falha ao cadastrar produto: " + e.getClass().getSimpleName());
            throw e;
        }
    }

    public List<Produto> listar() {
        logger.debug("Iniciando listagem de produtos.");
        List<Produto> produtos = repository.findAll();
        logger.info("Listagem de produtos concluída. Quantidade: " + produtos.size());
        return produtos;
    }

    public Optional<Produto> buscarPorId(Long id) {
        logger.debug("Buscando produto pelo ID: " + id);
        Optional<Produto> resultado = repository.findById(id);
        if (resultado.isPresent()) {
            logger.info("Produto encontrado. ID: " + id);
        } else {
            logger.erro("Produto não encontrado. ID: " + id);
        }
        return resultado;
    }

    @org.springframework.transaction.annotation.Transactional
    public Optional<Produto> atualizar(Long id, Produto dados) {
        try {
            logger.debug("Iniciando atualização de produto. ID: " + id);
            Optional<Produto> encontrado = repository.bloquear(id);
            if (encontrado.isEmpty()) {
                logger.erro("Produto não encontrado para atualização. ID: " + id);
                return Optional.empty();
            }
            Produto produto = encontrado.get();
            produto.setNome(dados.getNome());
            produto.setPreco(dados.getPreco());
            produto.setQuantidade(dados.getQuantidade());
            Produto atualizado = repository.save(produto);
            logger.info("Atualização concluída. ID: " + id);
            return Optional.of(atualizado);
        } catch (RuntimeException e) {
            logger.erro("Falha ao atualizar produto ID " + id + ": " + e.getClass().getSimpleName());
            throw e;
        }
    }

    public boolean excluir(Long id) {
        try {
            logger.debug("Iniciando exclusão de produto. ID: " + id);
            if (!repository.existsById(id)) {
                logger.erro("Produto não encontrado para exclusão. ID: " + id);
                return false;
            }
            repository.deleteById(id);
            logger.info("Exclusão concluída. ID: " + id);
            return true;
        } catch (RuntimeException e) {
            logger.erro("Falha ao excluir produto ID " + id + ": " + e.getClass().getSimpleName());
            throw e;
        }
    }
}
