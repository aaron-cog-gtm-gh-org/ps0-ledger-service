package com.acme.ledger.service;

import com.acme.ledger.cache.BalanceCache;
import com.acme.ledger.client.BalanceClient;
import com.acme.ledger.model.AccountBalance;
import java.io.UncheckedIOException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LedgerBalanceService {
    private static final Logger log = LoggerFactory.getLogger(LedgerBalanceService.class);

    private final BalanceClient balanceClient;
    private final BalanceCache balanceCache;

    public LedgerBalanceService(BalanceClient balanceClient, BalanceCache balanceCache) {
        this.balanceClient = balanceClient;
        this.balanceCache = balanceCache;
    }

    public AccountBalance currentBalance(String accountId) {
        try {
            AccountBalance balance = balanceClient.fetchBalance(accountId);
            balanceCache.put(balance);
            return balance;
        } catch (UncheckedIOException e) {
            if (e.getCause() instanceof HttpHostConnectException) {
                AccountBalance cached = balanceCache.lastKnown(accountId);
                if (cached != null) {
                    log.warn("Core banking unreachable, serving last known balance for account {}", accountId);
                    return cached;
                }
            }
            throw e;
        }
    }
}
