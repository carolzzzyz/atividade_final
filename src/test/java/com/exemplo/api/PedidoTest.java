package com.exemplo.api;
import com.exemplo.api.cep.*;
import com.exemplo.api.entity.*;
import com.exemplo.api.repository.*;
import com.exemplo.api.service.PedidoService;
import com.exemplo.api.state.*;
import com.exemplo.api.strategy.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:pedidos;DB_CLOSE_DELAY=-1")
class PedidoTest {
 @Autowired PedidoService service;
 @Autowired ProdutoRepository produtos;
 @MockBean ConsultaCep cep;
 @Test void fluxoEEstoque(){
  when(cep.buscar("01001000")).thenReturn(new Endereco("01001000","Praça da Sé","","Sé","São Paulo","SP"));
  Long id=produtos.save(new Produto("Teclado",100,5)).getId();
  Pedido p=service.criar(new PedidoService.Criacao(id,2,FormaPagamento.PIX,"01001000"));
  assertEquals(0,new BigDecimal("180.00").compareTo(p.getTotal()));
  assertEquals(3,produtos.findById(id).orElseThrow().getQuantidade());
  assertThrows(ResponseStatusException.class,()->service.executar(p.getId(),AcaoPedido.ENVIAR));
  assertThrows(ResponseStatusException.class,()->service.criar(new PedidoService.Criacao(id,4,FormaPagamento.PIX,"01001000")));
  service.executar(p.getId(),AcaoPedido.CONFIRMAR);
  service.executar(p.getId(),AcaoPedido.CANCELAR);
  assertEquals(5,produtos.findById(id).orElseThrow().getQuantidade());
  assertThrows(ResponseStatusException.class,()->service.executar(p.getId(),AcaoPedido.CANCELAR));
  Pedido q=service.criar(new PedidoService.Criacao(id,1,FormaPagamento.CARTAO,"01001000"));
  service.executar(q.getId(),AcaoPedido.CONFIRMAR);
  service.executar(q.getId(),AcaoPedido.ENVIAR);
  assertEquals(StatusPedido.FINALIZADO,service.executar(q.getId(),AcaoPedido.FINALIZAR).getStatus());
 }
 @Test void estrategias(){
  BigDecimal valor=new BigDecimal("200.00");
  assertEquals(new BigDecimal("20.00"),new DescontoPix().calcular(valor));
  assertEquals(new BigDecimal("10.00"),new DescontoBoleto().calcular(valor));
  assertEquals(new BigDecimal("0.00"),new DescontoCartao().calcular(valor));
 }
}
