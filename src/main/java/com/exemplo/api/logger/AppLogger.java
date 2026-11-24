package com.exemplo.api.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AppLogger {
    private static volatile AppLogger instancia;

    private AppLogger() {}

    public static AppLogger getInstance() {
        if (instancia == null) {
            synchronized (AppLogger.class) {
                if (instancia == null) {
                    instancia = new AppLogger();
                }
            }
        }
        return instancia;
    }

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
