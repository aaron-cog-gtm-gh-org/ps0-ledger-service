package com.acme.ledger.service;

import com.acme.ledger.model.LedgerPosting;
import com.acme.ledger.model.PostingReceipt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class LedgerPostingService {
    private final AtomicLong sequence = new AtomicLong();
    private final ConcurrentHashMap<String, LedgerPosting> postings = new ConcurrentHashMap<>();

    public PostingReceipt post(LedgerPosting posting) {
        String postingId = "PST-%06d".formatted(sequence.incrementAndGet());
        postings.put(postingId, posting);
        return new PostingReceipt(postingId, "accepted");
    }
}
