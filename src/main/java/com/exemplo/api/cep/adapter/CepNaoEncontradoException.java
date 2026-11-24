package com.exemplo.api.cep.adapter;

public class CepNaoEncontradoException extends RuntimeException {
    public CepNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
