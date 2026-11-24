package com.exemplo.api.cep.proxy;

public class CepInvalidoException extends RuntimeException {
    public CepInvalidoException(String mensagem) {
        super(mensagem);
    }
}
