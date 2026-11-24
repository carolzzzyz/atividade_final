package com.exemplo.api.strategy;
import java.math.BigDecimal;
import com.exemplo.api.entity.FormaPagamento;
public interface DescontoStrategy {
 FormaPagamento forma();
 BigDecimal calcular(BigDecimal subtotal);
}
