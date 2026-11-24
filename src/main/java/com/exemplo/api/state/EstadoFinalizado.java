package com.exemplo.api.state;
import com.exemplo.api.entity.StatusPedido;
import org.springframework.stereotype.Component;
@Component
public class EstadoFinalizado implements PedidoState {
 public StatusPedido status() { return StatusPedido.FINALIZADO; }
 
}
