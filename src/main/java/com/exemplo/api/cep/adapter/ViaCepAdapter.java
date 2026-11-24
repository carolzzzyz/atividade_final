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

public class ViaCepAdapter implements ConsultaCep {
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final AppLogger logger = AppLogger.getInstance();

    @Override
    public Endereco buscar(String cep) {
        try {
            logger.debug("Adapter ViaCEP consultando o CEP no provedor.");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
                    .GET().build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("ViaCEP respondeu HTTP " + response.statusCode());
            }

            JsonNode json = mapper.readTree(response.body());
            if (json.has("erro") && json.get("erro").asBoolean()) {
                throw new CepNaoEncontradoException("CEP não encontrado.");
            }

            Endereco endereco = new Endereco(
                    json.path("cep").asText(),
                    json.path("logradouro").asText(),
                    json.path("complemento").asText(),
                    json.path("bairro").asText(),
                    json.path("localidade").asText(),
                    json.path("uf").asText()
            );
            logger.info("Adapter ViaCEP converteu a resposta para Endereco.");
            return endereco;
        } catch (CepNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.erro("Falha no adapter ViaCEP: " + e.getClass().getSimpleName());
            throw new IllegalStateException("Não foi possível consultar o ViaCEP.", e);
        }
    }
}
