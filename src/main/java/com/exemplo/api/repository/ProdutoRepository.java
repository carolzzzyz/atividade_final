package com.exemplo.api.repository;

import com.exemplo.api.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Produto p where p.id = :id")
 Optional<Produto> bloquear(@org.springframework.data.repository.query.Param("id") Long id);
}
