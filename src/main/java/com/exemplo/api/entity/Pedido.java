package com.exemplo.api.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import com.exemplo.api.cep.Endereco;
@Entity
public class Pedido {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Produto produto;
 private int quantidade;
 private String nomeProduto;
 private BigDecimal precoUnitario, subtotal, desconto, total;
 @Enumerated(EnumType.STRING) private FormaPagamento pagamento;
 @Enumerated(EnumType.STRING) private StatusPedido status;
 private String cep, logradouro, bairro, cidade, estado;
 protected Pedido() {}
 public Pedido(Produto produto, int quantidade, FormaPagamento pagamento, BigDecimal preco, BigDecimal subtotal, BigDecimal desconto, Endereco endereco) {
  this.produto=produto; this.quantidade=quantidade; this.pagamento=pagamento; this.nomeProduto=produto.getNome(); this.precoUnitario=preco; this.subtotal=subtotal; this.desconto=desconto; this.total=subtotal.subtract(desconto); this.status=StatusPedido.PENDENTE;
  this.cep=endereco.getCep(); this.logradouro=endereco.getLogradouro(); this.bairro=endereco.getBairro(); this.cidade=endereco.getCidade(); this.estado=endereco.getEstado();
 }
 @com.fasterxml.jackson.annotation.JsonIgnore public Produto getProduto(){return produto;}
 public Long getProdutoId(){return produto.getId();}
 public Long getId(){return id;}
 public int getQuantidade(){return quantidade;}
 public String getNomeProduto(){return nomeProduto;}
 public BigDecimal getPrecoUnitario(){return precoUnitario;}
 public BigDecimal getSubtotal(){return subtotal;}
 public BigDecimal getDesconto(){return desconto;}
 public BigDecimal getTotal(){return total;}
 public FormaPagamento getPagamento(){return pagamento;}
 public StatusPedido getStatus(){return status;}
 public void alterarStatus(StatusPedido status){this.status=status;}
 public String getCep(){return cep;}
 public String getLogradouro(){return logradouro;}
 public String getBairro(){return bairro;}
 public String getCidade(){return cidade;}
 public String getEstado(){return estado;}
}
