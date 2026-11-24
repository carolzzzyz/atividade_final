package com.exemplo.api.controller;
import com.exemplo.api.entity.Pedido;
import com.exemplo.api.service.PedidoService;
import com.exemplo.api.state.AcaoPedido;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/pedidos")
public class PedidoController {
 private final PedidoService service;
 public PedidoController(PedidoService service){this.service=service;}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Pedido criar(@RequestBody PedidoService.Criacao dados){return service.criar(dados);}
 @GetMapping public List<Pedido> listar(){return service.listar();}
 @GetMapping("/{id}") public Pedido buscar(@PathVariable Long id){return service.buscar(id);}
 @PostMapping("/{id}/confirmar") public Pedido confirmar(@PathVariable Long id){return service.executar(id,AcaoPedido.CONFIRMAR);}
 @PostMapping("/{id}/enviar") public Pedido enviar(@PathVariable Long id){return service.executar(id,AcaoPedido.ENVIAR);}
 @PostMapping("/{id}/finalizar") public Pedido finalizar(@PathVariable Long id){return service.executar(id,AcaoPedido.FINALIZAR);}
 @PostMapping("/{id}/cancelar") public Pedido cancelar(@PathVariable Long id){return service.executar(id,AcaoPedido.CANCELAR);}
}
