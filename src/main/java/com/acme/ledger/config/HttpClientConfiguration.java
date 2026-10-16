package com.acme.ledger.config;

import com.acme.commons.http.AcmeHttpClient;
import com.acme.commons.http.HttpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {
    @Bean(destroyMethod = "close")
    AcmeHttpClient acmeHttpClient(CoreBankingProperties properties) {
        return HttpClientFactory.create(properties.baseUrl(), properties.timeout());
    }
}
