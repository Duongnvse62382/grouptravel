package com.fpt.gta.config;

import com.posadskiy.currencyconverter.CurrencyConverter;
import com.posadskiy.currencyconverter.config.ConfigBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CurrencyConverterConfig {
    @Value("${config.api.CurrencyConverterApiKey}")
    public String CurrencyConverterApiKey;

    @Bean
    public CurrencyConverter currencyConverter() {
        CurrencyConverter converter = new CurrencyConverter(
                new ConfigBuilder().currencyConverterApiApiKey(CurrencyConverterApiKey)
                        .build()
        );
        return converter;
    }
}
