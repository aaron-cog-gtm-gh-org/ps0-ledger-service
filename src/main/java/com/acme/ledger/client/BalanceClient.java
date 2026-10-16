package com.acme.ledger.client;

import com.acme.commons.http.AcmeHttpClient;
import com.acme.ledger.model.AccountBalance;
import org.springframework.stereotype.Component;

@Component
public class BalanceClient {
    private final AcmeHttpClient httpClient;

    public BalanceClient(AcmeHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public AccountBalance fetchBalance(String accountId) {
        return httpClient.get("/v1/accounts/" + accountId + "/balance", AccountBalance.class);
    }
}
