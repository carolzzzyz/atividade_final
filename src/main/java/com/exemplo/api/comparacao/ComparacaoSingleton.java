package com.exemplo.api.comparacao;

/*
 * Respostas solicitadas no enunciado.
 *
 * 1. Quem garante a instância única em cada versão?
 * No Singleton clássico, a própria classe controla a criação da instância
 * com construtor privado e getInstance(). No Spring, o contêiner gerencia
 * uma instância do componente por contexto, usando singleton por padrão.
 *
 * 2. Quando o Singleton clássico ainda faz sentido?
 * Quando é necessário controlar manualmente a criação de uma instância
 * sem depender de framework. Em aplicações Spring, injeção de dependências
 * geralmente facilita testes e manutenção.
 */
public class ComparacaoSingleton {
}
