package com.belenits.cashflow.account.controller;

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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.belenits.cashflow.account.dto.response.AccountTypeCountResponse;
import com.belenits.cashflow.account.exception.GlobalExceptionHandle;
import com.belenits.cashflow.account.security.JwtService;
import com.belenits.cashflow.account.security.Permissions;
import com.belenits.cashflow.account.service.AccountTypeCountService;

@WebMvcTest(controllers = AccountTypeController.class)
@Import({GlobalExceptionHandle.class, AccountTypeControllerTest.MethodSecurityTestConfig.class})
class AccountTypeControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
        // Enables @PreAuthorize in this WebMvcTest slice
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountTypeCountService accountTypeService;

    @MockitoBean
    private JwtService jwtService;

    private List<AccountTypeCountResponse> sampleCounts;

    @BeforeEach
    void setUp() {
        sampleCounts = List.of(
                new AccountTypeCountResponse(1L, "BANK", "Bank Account", "Accounts held at banks", 3L),
                new AccountTypeCountResponse(2L, "CREDIT_CARD", "Credit Card", "Card accounts", 1L),
                new AccountTypeCountResponse(3L, "CASH_WALLET", "Cash / Wallet", "Wallet balances", 1L),
                new AccountTypeCountResponse(4L, "INVESTMENT", "Investment", "Investment accounts", 1L),
                new AccountTypeCountResponse(5L, "LOAN", "Loan", "Loan accounts", 0L));
    }

    @Test
    @WithMockUser(authorities = Permissions.ACCOUNT_VIEW)
    @DisplayName("GET /counts - returns 200 with all account types and counts")
    void getAccountTypeCount_validAuth_returnsOk() throws Exception {
        when(jwtService.extractPermissions("Bearer valid-token"))
                .thenReturn(List.of(Permissions.ACCOUNT_VIEW));
        when(jwtService.extractUserId("Bearer valid-token")).thenReturn(1L);
        when(accountTypeService.getAccountTypeCounts(1L)).thenReturn(sampleCounts);

        mockMvc.perform(get("/api/v1/account-types/counts")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Account type counts retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].typeCode").value("BANK"))
                .andExpect(jsonPath("$.data[0].accountCount").value(3))
                .andExpect(jsonPath("$.data[4].typeCode").value("LOAN"))
                .andExpect(jsonPath("$.data[4].accountCount").value(0));
    }

    @Test
    @WithMockUser(authorities = Permissions.ACCOUNT_VIEW)
    @DisplayName("GET /counts - user with no accounts returns 200 with empty data")
    void getAccountTypeCount_noAccounts_returnsOkWithEmptyData() throws Exception {
        when(jwtService.extractPermissions("Bearer valid-token"))
                .thenReturn(List.of(Permissions.ACCOUNT_VIEW));
        when(jwtService.extractUserId("Bearer valid-token")).thenReturn(99L);
        when(accountTypeService.getAccountTypeCounts(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/account-types/counts")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(authorities = Permissions.ACCOUNT_VIEW)
    @DisplayName("GET /counts - missing Authorization header returns 401 via GlobalExceptionHandle")
    void getAccountTypeCount_missingAuthorization_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/account-types/counts"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Authentication context missing or invalid"));
    }


    @Test
    @WithMockUser(authorities = Permissions.ACCOUNT_VIEW)
    @DisplayName("GET /counts - jwtService extractUserId failure returns 500")
    void getAccountTypeCount_jwtExtractionFails_returnsInternalServerError() throws Exception {
        when(jwtService.extractPermissions("Bearer bad-token"))
                .thenReturn(List.of(Permissions.ACCOUNT_VIEW));
        when(jwtService.extractUserId("Bearer bad-token"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        mockMvc.perform(get("/api/v1/account-types/counts")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Internal server error"));
    }

    @Test
    @WithMockUser(authorities = Permissions.ACCOUNT_VIEW)
    @DisplayName("GET /counts - service failure returns 500")
    void getAccountTypeCount_serviceThrows_returnsInternalServerError() throws Exception {
        when(jwtService.extractPermissions("Bearer valid-token"))
                .thenReturn(List.of(Permissions.ACCOUNT_VIEW));
        when(jwtService.extractUserId("Bearer valid-token")).thenReturn(1L);
        when(accountTypeService.getAccountTypeCounts(1L))
                .thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(get("/api/v1/account-types/counts")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Internal server error"));
    }

    @Test
    @WithMockUser(authorities = Permissions.ACCOUNT_VIEW)
    @DisplayName("GET /counts - null service response currently returns 500")
    void getAccountTypeCount_serviceReturnsNull_returnsInternalServerError() throws Exception {
        when(jwtService.extractPermissions("Bearer valid-token"))
                .thenReturn(List.of(Permissions.ACCOUNT_VIEW));
        when(jwtService.extractUserId("Bearer valid-token")).thenReturn(1L);
        when(accountTypeService.getAccountTypeCounts(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/account-types/counts")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Internal server error"));
    }
}
