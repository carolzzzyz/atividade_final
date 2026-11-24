# API de Produtos — CRUD e Singleton Logger

## Repositório anterior e continuidade do projeto

Esta versão é uma evolução do projeto desenvolvido nas atividades anteriores da disciplina de Padrões de Projeto. O repositório anterior permanece disponível para consulta do histórico e das implementações originais. A versão atual está em um novo repositório e acrescenta o gerenciamento de pedidos, os padrões comportamentais Strategy e State e a interface web.

**Repositório anterior:** [atividade-padroes-projeto](https://github.com/carolzzzyz/atividade-padroes-projeto)


Projeto acadêmico em Java 17, Spring Boot, Spring Data JPA e banco H2. O recurso escolhido foi `Produto`.

## Requisitos
- Java JDK 17 ou superior
- Maven 3.6 ou superior
- IDE Java (IntelliJ IDEA, Eclipse ou VS Code)

## Como executar
Na pasta do projeto, execute:

```bash
mvn spring-boot:run
```

A API ficará disponível em `http://localhost:8080`. O banco H2 usa arquivo local em `./data`, para manter os dados entre reinicializações. O arquivo `app.log` é criado na pasta em que a aplicação é iniciada.

## Rotas
| Método | Rota | Resposta esperada |
|---|---|---|
| POST | `/produtos` | `201 Created` |
| GET | `/produtos` | `200 OK` |
| GET | `/produtos/{id}` | `200 OK` ou `404 Not Found` |
| PUT | `/produtos/{id}` | `200 OK` ou `404 Not Found` |
| DELETE | `/produtos/{id}` | `204 No Content` ou `404 Not Found` |

### Exemplo de cadastro
Envie um `POST` para `http://localhost:8080/produtos` com o corpo JSON:

```json
{
  "nome": "Teclado",
  "preco": 120.5,
  "quantidade": 10
}
```

### Exemplo de atualização
Envie um `PUT` para `http://localhost:8080/produtos/1`:

```json
{
  "nome": "Teclado mecânico",
  "preco": 199.9,
  "quantidade": 8
}
```

### Teste rápido com curl
```bash
curl -i -X POST http://localhost:8080/produtos -H "Content-Type: application/json" -d "{\"nome\":\"Teclado\",\"preco\":120.5,\"quantidade\":10}"
curl -i http://localhost:8080/produtos
curl -i http://localhost:8080/produtos/1
curl -i -X PUT http://localhost:8080/produtos/1 -H "Content-Type: application/json" -d "{\"nome\":\"Teclado mecânico\",\"preco\":199.9,\"quantidade\":8}"
curl -i -X DELETE http://localhost:8080/produtos/1
```

## Logger Singleton clássico
A versão final usa `AppLogger`, que possui construtor privado, `getInstance()` com inicialização lazy e thread-safe usando `volatile`, `synchronized` e dupla checagem. Os registros são gravados no console e em `app.log`, com os níveis DEBUG, INFO e ERRO.

`AppLoggerSpring` é uma cópia alternativa anotada com `@Component`, incluída apenas para comparação. Ela não é usada na versão final.

## Respostas para a comparação
**Quem garante a instância única em cada versão?**  
No Singleton clássico, a própria classe controla a criação da instância por meio do construtor privado e do método `getInstance()`. Na versão Spring, o contêiner gerencia a instância do componente dentro do contexto da aplicação, usando o escopo singleton por padrão.

**Quando o Singleton clássico ainda faz sentido?**  
Ele faz sentido quando é necessário controlar manualmente uma instância e utilizar a classe sem depender de um framework. Em aplicações Spring, a injeção de dependências geralmente facilita testes, manutenção e substituição de implementações.

## Justificativa da versão final
Foi escolhido o Singleton clássico porque o objetivo do exercício é demonstrar a implementação manual do padrão, incluindo inicialização lazy e segurança para múltiplas threads. A versão Spring foi mantida separadamente para comparação, mas não é utilizada junto com o logger clássico.

## Observações
- O arquivo `app.log` incluído é um exemplo ilustrativo; a execução real acrescentará novos registros.
- Não são registrados dados sensíveis.
- O tratamento global de exceções evita retornar detalhes internos da aplicação ao cliente.


## Complemento: Adapter e Proxy
Veja `README-ADAPTER-PROXY.md` para a documentação da consulta de CEP, dos dois adapters, do proxy com cache e dos testes manuais.

## Evolução — pedidos e padrões comportamentais

A API agora permite pedidos de um produto por vez, com quantidade, endereço consultado pelo Adapter/Proxy existente e descontos simulados (não processa pagamentos reais). O estoque é reservado na criação e devolvido somente no cancelamento. Preço e nome são registrados no pedido para preservar o histórico. Produtos vinculados a pedidos não podem ser excluídos (409). Pedidos não são removidos, apenas cancelados. As operações de estoque e pedido são transacionais; bloqueios de escrita evitam reservas simultâneas incompatíveis.

| Método | Rota | Resultado |
|---|---|---|
| POST | /pedidos | 201, cria e reserva |
| GET | /pedidos | 200, lista |
| GET | /pedidos/{id} | 200 ou 404 |
| POST | /pedidos/{id}/confirmar | 200 ou 409 |
| POST | /pedidos/{id}/enviar | 200 ou 409 |
| POST | /pedidos/{id}/finalizar | 200 ou 409 |
| POST | /pedidos/{id}/cancelar | 200 ou 409 |

### Demonstração no Postman
1. POST http://localhost:8080/produtos com Content-Type application/json e corpo `{"nome":"Teclado","preco":100,"quantidade":5}`. Anote o ID.
2. POST http://localhost:8080/pedidos com o corpo abaixo (substitua produtoId pelo ID retornado). A consulta de CEP exige internet.

```json
{"produtoId":1,"quantidade":2,"pagamento":"PIX","cep":"01001000"}
```

3. Resultado esperado: subtotal 200, desconto 20, total 180, status PENDENTE. GET /produtos/{id} deve mostrar estoque 3.
4. POST /pedidos/{id}/enviar antes de confirmar: 409. Use o ID do pedido, não do produto.
5. POST /pedidos/{id}/confirmar, depois /enviar e /finalizar (sem corpo). Status FINALIZADO.
6. Crie outro pedido e cancele antes do envio: estoque restaurado. Repetir cancelamento retorna 409 sem alterar estoque.
7. Tente comprar mais unidades que o estoque: 409. Quantidade zero: 400. CEP inválido: 400. Produto inexistente: 404 (com CEP válido).
8. Compare PIX (10%), BOLETO (5%) e CARTAO (0%). Confira app.log e GET /cep/01001000 duas vezes para demonstrar cache.

### Padrões e arquitetura
- Strategy: interface DescontoStrategy e classes DescontoPix, DescontoBoleto e DescontoCartao. PedidoService recebe as implementações por injeção e escolhe pela forma de pagamento. Evita condicionais de cálculo e permite novas estratégias.
- State: interface PedidoState e cinco classes concretas. O Service delega a transição ao estado atual, persistido como enum. PENDENTE permite confirmar/cancelar; CONFIRMADO permite enviar/cancelar; ENVIADO permite finalizar; FINALIZADO e CANCELADO são terminais. Operações incompatíveis retornam 409.
- Singleton: AppLogger original registra criação, transições e erros.
- Adapter e Proxy: ConsultaCep é injetado no PedidoService, preservando escolha do provedor, validação e cache.
- Controller recebe requisições e delega; Service coordena regras e transações; Repository persiste; Entity guarda dados; strategy e state encapsulam comportamentos.

### SOLID
SRP: cálculo, transição, persistência e HTTP separados. OCP: novas estratégias podem ser adicionadas sem mudar o cálculo existente (novas formas exigem atualizar o enum). LSP: implementações respeitam contratos comuns. ISP: interfaces pequenas e específicas. DIP: Service depende das abstrações DescontoStrategy, PedidoState e ConsultaCep, recebidas por injeção.

### Testes
Execute `mvn test`. PedidoTest cobre descontos, reserva, estoque insuficiente, transição inválida, cancelamento sem duplicação e fluxo até finalização. Usa H2 em memória e consulta CEP simulada para não depender da internet. Execute também a demonstração manual acima para validar a integração real. Os testes foram fornecidos, mas não executados no ambiente de geração por ausência de Maven.

### Apresentação
Explique o problema de controlar vendas sem vender além do estoque; mostre o CRUD original e a criação de pedido; compare descontos e tente uma transição inválida; mostre as interfaces e implementações, a injeção no Service e o app.log. Destaque a separação de responsabilidades e a consistência do estoque. Pontos de atenção no desenvolvimento: persistir estados sem perder comportamento, evitar devolução duplicada e integrar CEP sem acoplar o serviço ao provedor. Relate as dificuldades efetivamente observadas ao testar.

## Interface web integrada

Execute `ApiApplication` no IntelliJ e abra http://localhost:8080/ no navegador. Os arquivos HTML, CSS e JavaScript em `src/main/resources/static` são servidos pelo Spring Boot, sem instalar Node.js. A interface usa as rotas REST existentes para CRUD, pedidos e CEP. Os valores da prévia são estimativas; o cálculo definitivo e as transições são realizados no Service. Para demonstrar State, tente enviar um pedido pendente: a API deve rejeitar a operação. A consulta CEP requer internet.
