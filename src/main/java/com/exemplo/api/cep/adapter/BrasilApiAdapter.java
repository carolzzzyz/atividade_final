package com.exemplo.api.cep.adapter;

import com.exemplo.api.cep.ConsultaCep;
import com.exemplo.api.cep.Endereco;
import com.exemplo.api.logger.AppLogger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BrasilApiAdapter implements ConsultaCep {
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final AppLogger logger = AppLogger.getInstance();

    @Override
    public Endereco buscar(String cep) {
        try {
            logger.debug("Adapter BrasilAPI consultando o CEP no provedor.");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://brasilapi.com.br/api/cep/v1/" + cep))
                    .GET().build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                throw new CepNaoEncontradoException("CEP não encontrado.");
            }
            if (response.statusCode() != 200) {
                throw new IllegalStateException("BrasilAPI respondeu HTTP " + response.statusCode());
            }

            JsonNode json = mapper.readTree(response.body());
            Endereco endereco = new Endereco(
                    json.path("cep").asText(),
                    json.path("street").asText(),
                    "",
                    json.path("neighborhood").asText(),
                    json.path("city").asText(),
                    json.path("state").asText()
            );
            logger.info("Adapter BrasilAPI converteu a resposta para Endereco.");
            return endereco;
        } catch (CepNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.erro("Falha no adapter BrasilAPI: " + e.getClass().getSimpleName());
            throw new IllegalStateException("Não foi possível consultar a BrasilAPI.", e);
        }
    }
}
