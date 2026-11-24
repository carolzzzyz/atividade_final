package com.exemplo.api.state;
import com.exemplo.api.entity.StatusPedido;
import org.springframework.stereotype.Component;
@Component
public class EstadoPendente implements PedidoState {
 public StatusPedido status() { return StatusPedido.PENDENTE; }
 public StatusPedido executar(AcaoPedido acao) { return switch(acao) { case CONFIRMAR -> StatusPedido.CONFIRMADO; case CANCELAR -> StatusPedido.CANCELADO; default -> PedidoState.super.executar(acao); }; }
}
