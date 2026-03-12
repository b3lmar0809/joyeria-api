package com.joyeria.joyeria_api.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * FlowConfig class
 *
 * @Version: 1.0.0 - 12 mar. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 12 mar. 2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FlowConfig {

    @Value("${flow.api.key}")
    private String apiKey;

    @Value("${flow.secret.key}")
    private String secretKey;

    @Value("${flow.api.url}")
    private String apiUrl;

    @Value("${flow.url.return}")
    private String urlReturn;

    @Value("${flow.url.cancel}")
    private String urlCancel;

    @Value("${flow.url.callback}")
    private String urlCallback;

    //para ver si se creo corretamente
    @PostConstruct
    public void init() {
        log.info(" Flow configurado correctamente");
        log.info("API URL: {}", apiUrl);
        log.info("Return URL: {}", urlReturn);
        log.info("Callback URL: {}", urlCallback);
    }

    @Bean
    public WebClient.Builder wedClienteBuilder() {
        return WebClient.builder();
    }

}
