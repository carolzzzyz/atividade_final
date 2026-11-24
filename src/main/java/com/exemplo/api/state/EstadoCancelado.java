package com.exemplo.api.state;
import com.exemplo.api.entity.StatusPedido;
import org.springframework.stereotype.Component;
@Component
public class EstadoCancelado implements PedidoState {
 public StatusPedido status() { return StatusPedido.CANCELADO; }
 
}
