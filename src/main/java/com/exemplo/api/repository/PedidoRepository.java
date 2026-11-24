package com.exemplo.api.repository;
import com.exemplo.api.entity.Pedido;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Pedido p where p.id = :id")
 Optional<Pedido> bloquear(@org.springframework.data.repository.query.Param("id") Long id);
}
