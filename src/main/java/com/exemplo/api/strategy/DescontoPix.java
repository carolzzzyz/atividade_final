package com.exemplo.api.strategy;
import java.math.*;
import com.exemplo.api.entity.FormaPagamento;
import org.springframework.stereotype.Component;
@Component
public class DescontoPix implements DescontoStrategy {
 public FormaPagamento forma() { return FormaPagamento.PIX; }
 public BigDecimal calcular(BigDecimal subtotal) { return subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP); }
}
