package com.exemplo.api.state;
import com.exemplo.api.entity.StatusPedido;
import org.springframework.stereotype.Component;
@Component
public class EstadoConfirmado implements PedidoState {
 public StatusPedido status() { return StatusPedido.CONFIRMADO; }
 public StatusPedido executar(AcaoPedido acao) { return switch(acao) { case ENVIAR -> StatusPedido.ENVIADO; case CANCELAR -> StatusPedido.CANCELADO; default -> PedidoState.super.executar(acao); }; }
}
