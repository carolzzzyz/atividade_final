package com.exemplo.api.cep;

import com.exemplo.api.cep.adapter.BrasilApiAdapter;
import com.exemplo.api.cep.adapter.ViaCepAdapter;
import com.exemplo.api.cep.proxy.ConsultaCepProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CepConfig {

    @Bean
    public ConsultaCep consultaCep(
            @Value("${app.cep.provedor:viacep}") String provedor) {

        // Os adapters são criados aqui para que o Controller receba somente
        // um bean ConsultaCep: o Proxy que envolve o provedor selecionado.
        ViaCepAdapter viaCepAdapter = new ViaCepAdapter();
        BrasilApiAdapter brasilApiAdapter = new BrasilApiAdapter();

        // A escolha do provedor fica centralizada aqui.
        // Para trocar, altere somente app.cep.provedor no application.properties.
        ConsultaCep adapterEscolhido = provedor.equalsIgnoreCase("brasilapi")
                ? brasilApiAdapter
                : viaCepAdapter;

        // O Controller recebe ConsultaCep e não sabe que existe um Proxy.
        return new ConsultaCepProxy(adapterEscolhido);
    }
}
