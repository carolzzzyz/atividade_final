package com.exemplo.api.state;
import com.exemplo.api.entity.StatusPedido;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
public interface PedidoState {
 StatusPedido status();
 default StatusPedido executar(AcaoPedido acao) {
  throw new ResponseStatusException(HttpStatus.CONFLICT, "Operação " + acao + " inválida para " + status());
 }
}
