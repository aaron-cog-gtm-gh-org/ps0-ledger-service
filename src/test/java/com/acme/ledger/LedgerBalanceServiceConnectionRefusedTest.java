package com.acme.ledger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acme.commons.http.AcmeHttpClient;
import com.acme.commons.http.HttpClientFactory;
import com.acme.ledger.cache.BalanceCache;
import com.acme.ledger.client.BalanceClient;
import com.acme.ledger.model.AccountBalance;
import com.acme.ledger.service.LedgerBalanceService;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class LedgerBalanceServiceConnectionRefusedTest {
    @Test
    void returnsCachedBalanceWhenCoreBankingConnectionIsRefused() throws Exception {
        int port = freePort();
        BalanceCache balanceCache = new BalanceCache();
        AccountBalance cached = balance();
        balanceCache.put(cached);

        try (AcmeHttpClient httpClient =
                HttpClientFactory.create("http://127.0.0.1:" + port, Duration.ofSeconds(2))) {
            LedgerBalanceService service = new LedgerBalanceService(new BalanceClient(httpClient), balanceCache);

            assertThat(service.currentBalance(cached.accountId())).isEqualTo(cached);
        }
    }

    @Test
    void propagatesConnectionFailureWhenNoCachedBalanceExists() throws Exception {
        int port = freePort();
        BalanceCache balanceCache = new BalanceCache();

        try (AcmeHttpClient httpClient =
                HttpClientFactory.create("http://127.0.0.1:" + port, Duration.ofSeconds(2))) {
            LedgerBalanceService service = new LedgerBalanceService(new BalanceClient(httpClient), balanceCache);

            assertThatThrownBy(() -> service.currentBalance("ACC-100234"))
                    .isInstanceOf(UncheckedIOException.class)
                    .hasCauseInstanceOf(ConnectException.class);
        }
    }

    private int freePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private AccountBalance balance() {
        return new AccountBalance(
                "ACC-100234", "USD", new BigDecimal("1250.75"), new BigDecimal("1300.00"), "2024-10-15T10:15:30Z");
    }
}
