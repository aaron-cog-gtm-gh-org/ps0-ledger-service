package com.acme.ledger.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.core-banking")
public record CoreBankingProperties(String baseUrl, Duration timeout) {
}
