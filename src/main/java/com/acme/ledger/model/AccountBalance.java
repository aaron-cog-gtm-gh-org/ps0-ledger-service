package com.acme.ledger.model;

import java.math.BigDecimal;

public record AccountBalance(
        String accountId,
        String currency,
        BigDecimal availableBalance,
        BigDecimal ledgerBalance,
        String asOf) {
}
