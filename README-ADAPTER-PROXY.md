# Complemento do trabalho — Adapter e Proxy

Este projeto estende a API de Produtos do trabalho anterior. O CRUD e o `AppLogger` Singleton foram mantidos.

## Como executar
Requer Java 17 e Maven. Na pasta do projeto:
```bash
mvn spring-boot:run
```

## Consulta de CEP
Rota:
`GET http://localhost:8080/cep/01001000`

A rota retorna um objeto `Endereco` com os campos `cep`, `logradouro`, `complemento`, `bairro`, `cidade` e `estado`.

### Trocar o provedor
No arquivo `src/main/resources/application.properties`, altere:
```properties
app.cep.provedor=viacep
```
Para usar a BrasilAPI:
```properties
app.cep.provedor=brasilapi
```
A seleção está centralizada em `CepConfig`. Os Controllers e demais classes conhecem somente a interface `ConsultaCep`.

## Adapter
- `ConsultaCep` é a interface interna do sistema.
- `ViaCepAdapter` consulta `https://viacep.com.br/ws/{cep}/json/` e converte os campos `localidade` e `uf`.
- `BrasilApiAdapter` consulta `https://brasilapi.com.br/api/cep/v1/{cep}` e converte `city` e `state`.
- Ambos entregam o mesmo objeto `Endereco`, apesar dos JSONs diferentes.

## Proxy
`ConsultaCepProxy` implementa a mesma interface `ConsultaCep` e envolve o adapter selecionado. Antes de chamar o provedor:
1. Valida se o CEP contém exatamente oito dígitos.
2. Procura o CEP em um `Map` em memória.
3. Se estiver no cache, devolve o endereço sem chamar a API.
4. Se não estiver, consulta o adapter, guarda o resultado e devolve.

O método é sincronizado para proteger o `HashMap` contra acessos concorrentes. O cache existe enquanto a aplicação estiver em execução e é perdido ao reiniciar.

## Logger
O `AppLogger` registra:
- Adapter escolhido e conversão da resposta;
- CEP inválido bloqueado pelo Proxy;
- acerto de cache;
- chamada ao provedor e armazenamento da resposta.

Nenhum dado sensível é registrado.

## Testes manuais
1. Faça `GET /cep/01001000`: deve consultar o provedor e guardar o resultado no cache.
2. Repita `GET /cep/01001000`: deve retornar do cache. Confira `app.log`.
3. Faça `GET /cep/123`: deve retornar `400 Bad Request` sem chamar o provedor.
4. Teste um CEP inexistente, por exemplo `00000000`: o provedor pode responder que não foi encontrado, retornando `404`.
5. Teste novamente as cinco rotas `/produtos` para confirmar que o CRUD anterior continua funcionando.

## Decisões de implementação
Os dois adapters usam chamadas HTTP reais, portanto a consulta de CEP depende de conexão com a internet e da disponibilidade do serviço escolhido. A escolha do provedor é feita por uma propriedade de configuração. O Proxy é criado na configuração Spring e injetado como `ConsultaCep`, sem que o Controller tenha conhecimento de sua existência.
