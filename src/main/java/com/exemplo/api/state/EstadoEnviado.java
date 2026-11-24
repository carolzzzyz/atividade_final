package com.exemplo.api.state;
import com.exemplo.api.entity.StatusPedido;
import org.springframework.stereotype.Component;
@Component
public class EstadoEnviado implements PedidoState {
 public StatusPedido status() { return StatusPedido.ENVIADO; }
 public StatusPedido executar(AcaoPedido acao) { return switch(acao) { case FINALIZAR -> StatusPedido.FINALIZADO; default -> PedidoState.super.executar(acao); }; }
}
