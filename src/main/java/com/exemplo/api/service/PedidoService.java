package com.exemplo.api.service;
import com.exemplo.api.entity.*;
import com.exemplo.api.repository.*;
import com.exemplo.api.strategy.*;
import com.exemplo.api.state.*;
import com.exemplo.api.cep.*;
import com.exemplo.api.logger.AppLogger;
import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
@Transactional
public class PedidoService {
 private final PedidoRepository pedidos;
 private final ProdutoRepository produtos;
 private final ConsultaCep cep;
 private final List<DescontoStrategy> descontos;
 private final List<PedidoState> estados;
 private final AppLogger logger=AppLogger.getInstance();
 public PedidoService(PedidoRepository pedidos, ProdutoRepository produtos, ConsultaCep cep, List<DescontoStrategy> descontos, List<PedidoState> estados){this.pedidos=pedidos;this.produtos=produtos;this.cep=cep;this.descontos=descontos;this.estados=estados;}
 public record Criacao(Long produtoId, Integer quantidade, FormaPagamento pagamento, String cep) {}
 public Pedido criar(Criacao dados){
  logger.debug("Criando pedido");
  if(dados==null || dados.produtoId()==null || dados.quantidade()==null || dados.quantidade()<=0 || dados.pagamento()==null || dados.cep()==null || dados.cep().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Informe produto, quantidade positiva, pagamento e CEP");
  Endereco endereco=cep.buscar(dados.cep());
  Produto produto=produtos.bloquear(dados.produtoId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado"));
  if(produto.getQuantidade()<dados.quantidade()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Estoque insuficiente");
  if(!Double.isFinite(produto.getPreco()) || produto.getPreco()<0) throw new ResponseStatusException(HttpStatus.CONFLICT,"Preço inválido");
  BigDecimal preco=BigDecimal.valueOf(produto.getPreco()).setScale(2,RoundingMode.HALF_UP);
  BigDecimal subtotal=preco.multiply(BigDecimal.valueOf(dados.quantidade()));
  DescontoStrategy estrategia=descontos.stream().filter(d->d.forma()==dados.pagamento()).findFirst().orElseThrow();
  Pedido pedido=new Pedido(produto,dados.quantidade(),dados.pagamento(),preco,subtotal,estrategia.calcular(subtotal),endereco);
  produto.setQuantidade(produto.getQuantidade()-dados.quantidade());
  Pedido salvo=pedidos.save(pedido);
  logger.info("Pedido criado e estoque reservado: "+salvo.getId()); return salvo;
 }
 @Transactional(readOnly=true) public List<Pedido> listar(){return pedidos.findAll();}
 @Transactional(readOnly=true) public Pedido buscar(Long id){return pedidos.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido não encontrado"));}
 public Pedido executar(Long id, AcaoPedido acao){
  logger.debug("Pedido "+id+": "+acao);
  Pedido pedido=pedidos.bloquear(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido não encontrado"));
  PedidoState estado=estados.stream().filter(e->e.status()==pedido.getStatus()).findFirst().orElseThrow();
  StatusPedido novo=estado.executar(acao);
  if(novo==StatusPedido.CANCELADO){
   Produto produto=produtos.bloquear(pedido.getProdutoId()).orElseThrow();
   produto.setQuantidade(Math.addExact(produto.getQuantidade(),pedido.getQuantidade()));
  }
  pedido.alterarStatus(novo); logger.info("Pedido "+id+" alterado para "+novo); return pedido;
 }
}
