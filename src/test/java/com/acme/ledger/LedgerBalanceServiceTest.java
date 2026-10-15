package com.acme.ledger;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acme.commons.http.AcmeHttpClient;
import com.acme.commons.http.AcmeHttpException;
import com.acme.commons.http.HttpClientFactory;
import com.acme.ledger.cache.BalanceCache;
import com.acme.ledger.client.BalanceClient;
import com.acme.ledger.model.AccountBalance;
import com.acme.ledger.service.LedgerBalanceService;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LedgerBalanceServiceTest {
    private static final String BALANCE_JSON = """
            {
              "accountId": "ACC-100234",
              "currency": "USD",
              "availableBalance": 1250.75,
              "ledgerBalance": 1300.00,
              "asOf": "2024-10-15T10:15:30Z"
            }
            """;

    private WireMockServer server;
    private AcmeHttpClient httpClient;
    private BalanceCache balanceCache;
    private LedgerBalanceService service;

    @BeforeEach
    void setUp() {
        server = new WireMockServer(com.github.tomakehurst.wiremock.core.WireMockConfiguration.options().dynamicPort());
        server.start();
        httpClient = HttpClientFactory.create(server.baseUrl());
        balanceCache = new BalanceCache();
        service = new LedgerBalanceService(new BalanceClient(httpClient), balanceCache);
    }

    @AfterEach
    void tearDown() {
        httpClient.close();
        server.stop();
    }

    @Test
    void fetchesAndCachesBalance() {
        server.stubFor(get(urlEqualTo("/v1/accounts/ACC-100234/balance"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(BALANCE_JSON)));

        AccountBalance balance = service.currentBalance("ACC-100234");

        assertThat(balance).isEqualTo(expectedBalance());
        assertThat(balanceCache.lastKnown("ACC-100234")).isEqualTo(expectedBalance());
    }

    @Test
    void propagatesNotFoundResponseWithoutUsingFallback() {
        server.stubFor(get(urlEqualTo("/v1/accounts/ACC-100234/balance"))
                .willReturn(aResponse().withStatus(404).withBody("Account not found")));

        assertThatThrownBy(() -> service.currentBalance("ACC-100234"))
                .isInstanceOfSatisfying(AcmeHttpException.class, error -> assertThat(error.status()).isEqualTo(404));
    }

    private AccountBalance expectedBalance() {
        return new AccountBalance(
                "ACC-100234", "USD", new BigDecimal("1250.75"), new BigDecimal("1300.00"), "2024-10-15T10:15:30Z");
    }
}
