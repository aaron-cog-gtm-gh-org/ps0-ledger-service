package com.acme.ledger;

import com.acme.ledger.model.AccountBalance;
import com.acme.ledger.model.LedgerPosting;
import com.acme.ledger.model.PostingReceipt;
import com.acme.ledger.service.LedgerBalanceService;
import com.acme.ledger.service.LedgerPostingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class LedgerController {
    private final LedgerBalanceService balanceService;
    private final LedgerPostingService postingService;

    public LedgerController(LedgerBalanceService balanceService, LedgerPostingService postingService) {
        this.balanceService = balanceService;
        this.postingService = postingService;
    }

    @PostMapping("/postings")
    public ResponseEntity<PostingReceipt> post(@RequestBody LedgerPosting posting) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postingService.post(posting));
    }

    @GetMapping("/accounts/{id}/balance")
    public AccountBalance balance(@PathVariable String id) {
        return balanceService.currentBalance(id);
    }
}
