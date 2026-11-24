package com.exemplo.api.cep.proxy;

import com.exemplo.api.cep.ConsultaCep;
import com.exemplo.api.cep.Endereco;
import com.exemplo.api.logger.AppLogger;

import java.util.HashMap;
import java.util.Map;

public class ConsultaCepProxy implements ConsultaCep {
    private final ConsultaCep provedor;
    private final Map<String, Endereco> cache = new HashMap<>();
    private final AppLogger logger = AppLogger.getInstance();

    public ConsultaCepProxy(ConsultaCep provedor) {
        this.provedor = provedor;
    }

    @Override
    public synchronized Endereco buscar(String cep) {
        if (cep == null || !cep.matches("\\d{8}")) {
            logger.erro("Proxy bloqueou consulta: formato de CEP inválido.");
            throw new CepInvalidoException("O CEP deve conter exatamente 8 dígitos.");
        }

        if (cache.containsKey(cep)) {
            logger.info("Proxy: resposta do cache para o CEP " + cep + ".");
            return cache.get(cep);
        }

        logger.debug("Proxy: cache não encontrado; chamando o provedor para o CEP " + cep + ".");
        Endereco endereco = provedor.buscar(cep);
        cache.put(cep, endereco);
        logger.info("Proxy: resposta do provedor armazenada no cache para o CEP " + cep + ".");
        return endereco;
    }
}
