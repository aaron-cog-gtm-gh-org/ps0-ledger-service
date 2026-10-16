package com.acme.ledger;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acme.ledger.model.AccountBalance;
import com.acme.ledger.service.LedgerBalanceService;
import com.acme.ledger.service.LedgerPostingService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LedgerController.class)
@Import(LedgerPostingService.class)
class LedgerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LedgerBalanceService balanceService;

    @Test
    void acceptsPostingAndReturnsDeterministicReceipt() throws Exception {
        mockMvc.perform(post("/v1/postings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reference": "ref-1",
                                  "debitAccount": "debit-1",
                                  "creditAccount": "credit-1",
                                  "amount": 125.50,
                                  "currency": "CAD",
                                  "narrative": "Settlement ref-1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postingId").value("PST-000001"))
                .andExpect(jsonPath("$.status").value("accepted"));
    }

    @Test
    void getsBalanceThroughBalanceService() throws Exception {
        AccountBalance balance = new AccountBalance(
                "ACC-100234", "USD", new BigDecimal("1250.75"), new BigDecimal("1300.00"),
                "2024-10-15T10:15:30Z");
        when(balanceService.currentBalance("ACC-100234")).thenReturn(balance);

        mockMvc.perform(get("/v1/accounts/ACC-100234/balance"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value("ACC-100234"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.availableBalance").value(1250.75))
                .andExpect(jsonPath("$.ledgerBalance").value(1300.00))
                .andExpect(jsonPath("$.asOf").value("2024-10-15T10:15:30Z"));

        verify(balanceService).currentBalance("ACC-100234");
    }
}
