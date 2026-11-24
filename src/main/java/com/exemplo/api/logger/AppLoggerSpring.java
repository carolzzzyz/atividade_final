package com.exemplo.api.logger;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/*
 * Versão alternativa para comparação. Não é utilizada na versão final:
 * a API utiliza o Singleton clássico AppLogger.
 */
@Component
public class AppLoggerSpring {
    public void debug(String mensagem) { registrar("DEBUG", mensagem); }
    public void info(String mensagem) { registrar("INFO", mensagem); }
    public void erro(String mensagem) { registrar("ERRO", mensagem); }

    private synchronized void registrar(String nivel, String mensagem) {
        String linha = LocalDateTime.now() + " [" + nivel + "] " + mensagem;
        System.out.println(linha);
        try (PrintWriter arquivo = new PrintWriter(new FileWriter("app.log", true))) {
            arquivo.println(linha);
        } catch (IOException e) {
            System.err.println("Não foi possível escrever no arquivo app.log.");
        }
    }
}
