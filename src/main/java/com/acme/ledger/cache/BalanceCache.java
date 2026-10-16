package com.acme.ledger.cache;

import com.acme.ledger.model.AccountBalance;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class BalanceCache {
    private final ConcurrentHashMap<String, AccountBalance> balances = new ConcurrentHashMap<>();

    public void put(AccountBalance balance) {
        balances.put(balance.accountId(), balance);
    }

    public AccountBalance lastKnown(String accountId) {
        return balances.get(accountId);
    }
}
