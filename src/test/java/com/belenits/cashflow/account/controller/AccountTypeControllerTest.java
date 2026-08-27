package com.belenits.cashflow.account.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.belenits.cashflow.account.dto.response.AccountTypeCountResponse;
import com.belenits.cashflow.account.exception.GlobalExceptionHandle;
import com.belenits.cashflow.account.security.JwtUtil;
import com.belenits.cashflow.account.service.AccountTypeCountService;

@WebMvcTest(controllers = AccountTypeController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandle.class)
class AccountTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountTypeCountService accountTypeService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private List<AccountTypeCountResponse> sampleCounts;

    @BeforeEach
    void setUp() {
        sampleCounts = List.of(
                new AccountTypeCountResponse(1L, "BANK", "Bank Account",
                        "Accounts held at banks and financial institutions", 3L),
                new AccountTypeCountResponse(2L, "CREDIT_CARD", "Credit Card",
                        "Credit card accounts and balances", 1L),
                new AccountTypeCountResponse(3L, "CASH_WALLET", "Cash / Wallet",
                        "Physical cash and digital wallets", 1L),
                new AccountTypeCountResponse(4L, "INVESTMENT", "Investment",
                        "Investment and brokerage accounts", 1L),
                new AccountTypeCountResponse(5L, "LOAN", "Loan",
                        "Loans and credit accounts", 0L));
    }

    @Test
    @DisplayName("GET /counts - returns 200 with all account types and their counts")
    void getAccountTypeCount_validAuthorization_returnsOk() throws Exception {
        when(jwtUtil.extractUserId("Bearer valid-token")).thenReturn(1L);
        when(accountTypeService.getAccountTypeCounts(1L)).thenReturn(sampleCounts);

        mockMvc.perform(get("/api/v1/account-types/counts")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].typeCode").value("BANK"))
                .andExpect(jsonPath("$.data[0].accountCount").value(3))
                .andExpect(jsonPath("$.data[4].typeCode").value("LOAN"))
                .andExpect(jsonPath("$.data[4].accountCount").value(0));

        verify(accountTypeService).getAccountTypeCounts(1L);
    }

    @Test
    @DisplayName("GET /counts - user with zero accounts returns all types with zero counts")
    void getAccountTypeCount_userWithNoAccounts_returnsAllTypesZeroed() throws Exception {
        List<AccountTypeCountResponse> allZero = List.of(
                new AccountTypeCountResponse(1L, "BANK", "Bank Account", "desc", 0L),
                new AccountTypeCountResponse(2L, "CREDIT_CARD", "Credit Card", "desc", 0L),
                new AccountTypeCountResponse(3L, "CASH_WALLET", "Cash / Wallet", "desc", 0L),
                new AccountTypeCountResponse(4L, "INVESTMENT", "Investment", "desc", 0L),
                new AccountTypeCountResponse(5L, "LOAN", "Loan", "desc", 0L));

        when(jwtUtil.extractUserId("Bearer valid-token")).thenReturn(99L);
        when(accountTypeService.getAccountTypeCounts(99L)).thenReturn(allZero);

        mockMvc.perform(get("/api/v1/account-types/counts")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].accountCount").value(0));

        verify(accountTypeService).getAccountTypeCounts(99L);
    }

    @Test
    @DisplayName("GET /counts - missing Authorization header returns 401")
    void getAccountTypeCount_missingAuthorization_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/account-types/counts"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Authentication context missing or invalid"));
    }
}
