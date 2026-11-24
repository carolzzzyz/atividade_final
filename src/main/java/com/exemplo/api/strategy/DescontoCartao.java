package com.exemplo.api.strategy;
import java.math.*;
import com.exemplo.api.entity.FormaPagamento;
import org.springframework.stereotype.Component;
@Component
public class DescontoCartao implements DescontoStrategy {
 public FormaPagamento forma() { return FormaPagamento.CARTAO; }
 public BigDecimal calcular(BigDecimal subtotal) { return subtotal.multiply(new BigDecimal("0.00")).setScale(2, RoundingMode.HALF_UP); }
}
