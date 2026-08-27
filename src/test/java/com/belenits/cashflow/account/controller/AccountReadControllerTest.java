package com.belenits.cashflow.account.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.belenits.cashflow.account.dto.response.AccountDetailResponse;
import com.belenits.cashflow.account.dto.response.AccountOverviewResponse;
import com.belenits.cashflow.account.dto.response.AccountSummaryResponse;
import com.belenits.cashflow.account.dto.response.AccountTypeSummaryResponse;
import com.belenits.cashflow.account.entity.AccountStatus;
import com.belenits.cashflow.account.exception.AccountAccessDeniedException;
import com.belenits.cashflow.account.exception.AccountNotFoundException;
import com.belenits.cashflow.account.exception.GlobalExceptionHandle;
import com.belenits.cashflow.account.security.JwtUtil;
import com.belenits.cashflow.account.service.AccountDetailService;
import com.belenits.cashflow.account.service.AccountOverviewService;

@WebMvcTest(controllers = AccountReadController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandle.class)
class AccountReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountOverviewService accountOverviewService;

    @MockitoBean
    private AccountDetailService accountDetailService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private AccountOverviewResponse sampleOverviewResponse;
    private AccountDetailResponse sampleDetailResponse;

    @BeforeEach
    void setUp() {
        AccountTypeSummaryResponse typeSummary = new AccountTypeSummaryResponse();
        typeSummary.setAccountTypeId(1L);
        typeSummary.setAccountTypeName("Bank Account");
        typeSummary.setTotalBalance(BigDecimal.valueOf(15000));
        typeSummary.setPercentage(BigDecimal.valueOf(100));

        AccountSummaryResponse accountSummary = new AccountSummaryResponse();
        accountSummary.setAccountId(1L);
        accountSummary.setAccountName("Main Checking");
        accountSummary.setAccountTypeName("Bank Account");
        accountSummary.setBalance(BigDecimal.valueOf(15000));
        accountSummary.setMaskedIdentifier("1234");
        accountSummary.setStatus(AccountStatus.ACTIVE);

        sampleOverviewResponse = new AccountOverviewResponse();
        sampleOverviewResponse.setTotalBalance(BigDecimal.valueOf(15000));
        sampleOverviewResponse.setTotalAccounts(1);
        sampleOverviewResponse.setAccountsByType(List.of(typeSummary));
        sampleOverviewResponse.setAccounts(List.of(accountSummary));

        sampleDetailResponse = new AccountDetailResponse();
        sampleDetailResponse.setAccountId(1L);
        sampleDetailResponse.setAccountName("Main Checking");
        sampleDetailResponse.setAccountTypeName("Bank Account");
        sampleDetailResponse.setTypeCode("BANK");
        sampleDetailResponse.setInstitutionName("HDFC Bank");
        sampleDetailResponse.setCurrencyCode("INR");
        sampleDetailResponse.setCurrentBalance(BigDecimal.valueOf(12450));
        sampleDetailResponse.setAvailableBalance(BigDecimal.valueOf(12300));
        sampleDetailResponse.setStatus(AccountStatus.ACTIVE);
        sampleDetailResponse.setRecentTransactions(List.of());
        sampleDetailResponse.setTransactionsLoadFailed(false);
    }

    // ==================== GET /overview ====================

    @Test
    @DisplayName("GET /overview - returns 200 with overview data for valid Authorization header")
    void getAccountOverview_validAuth_returnsOk() throws Exception {
        when(jwtUtil.extractUserId("Bearer valid-token")).thenReturn(1L);
        when(accountOverviewService.getAccountOverview(1L)).thenReturn(sampleOverviewResponse);

        mockMvc.perform(get("/api/v1/accounts/overview")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalBalance").value(15000))
                .andExpect(jsonPath("$.data.totalAccounts").value(1))
                .andExpect(jsonPath("$.data.accountsByType[0].accountTypeName").value("Bank Account"))
                .andExpect(jsonPath("$.data.accounts[0].accountName").value("Main Checking"));
    }

    @Test
    @DisplayName("GET /overview - missing Authorization header returns 401")
    void getAccountOverview_missingAuthorization_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/overview"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Authentication context missing or invalid"));
    }

    @Test
    @DisplayName("GET /overview - user with zero accounts returns 200 with empty/zero data")
    void getAccountOverview_userWithNoAccounts_returnsOkWithEmptyData() throws Exception {
        AccountOverviewResponse emptyResponse = new AccountOverviewResponse();
        emptyResponse.setTotalBalance(BigDecimal.ZERO);
        emptyResponse.setTotalAccounts(0);
        emptyResponse.setAccountsByType(List.of());
        emptyResponse.setAccounts(List.of());

        when(jwtUtil.extractUserId("Bearer valid-token")).thenReturn(99L);
        when(accountOverviewService.getAccountOverview(99L)).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/v1/accounts/overview")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalBalance").value(0))
                .andExpect(jsonPath("$.data.totalAccounts").value(0))
                .andExpect(jsonPath("$.data.accounts").isEmpty());
    }

    // ==================== GET /{accountId} ====================

    @Test
    @DisplayName("GET /{accountId} - returns 200 with account details for a valid request")
    void getAccountDetail_validRequest_returnsOk() throws Exception {
        when(jwtUtil.extractUserId("Bearer valid-token")).thenReturn(1L);
        when(accountDetailService.getAccountDetailById(1L, 1L)).thenReturn(sampleDetailResponse);

        mockMvc.perform(get("/api/v1/accounts/1")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accountId").value(1))
                .andExpect(jsonPath("$.data.typeCode").value("BANK"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /{accountId} - missing Authorization header returns 401")
    void getAccountDetail_missingAuthorization_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Authentication context missing or invalid"));
    }

    @Test
    @DisplayName("GET /{accountId} - negative accountId fails @Positive validation, returns 400")
    void getAccountDetail_negativeAccountId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/-1")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /{accountId} - zero accountId fails @Positive validation, returns 400")
    void getAccountDetail_zeroAccountId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/0")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /{accountId} - non-numeric accountId returns 400")
    void getAccountDetail_nonNumericAccountId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/abc")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /{accountId} - non-existent account returns 404")
    void getAccountDetail_accountNotFound_returnsNotFound() throws Exception {
        when(jwtUtil.extractUserId("Bearer valid-token")).thenReturn(1L);
        when(accountDetailService.getAccountDetailById(999L, 1L))
                .thenThrow(new AccountNotFoundException("Account with id 999 not found"));

        mockMvc.perform(get("/api/v1/accounts/999")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Account with id 999 not found"));
    }

    @Test
    @DisplayName("GET /{accountId} - account belonging to different user returns 403")
    void getAccountDetail_accountBelongsToDifferentUser_returnsForbidden() throws Exception {
        when(jwtUtil.extractUserId("Bearer other-user-token")).thenReturn(2L);
        when(accountDetailService.getAccountDetailById(1L, 2L))
                .thenThrow(new AccountAccessDeniedException("This account does not belong to the requesting user"));

        mockMvc.perform(get("/api/v1/accounts/1")
                .header("Authorization", "Bearer other-user-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("This account does not belong to the requesting user"));
    }

    @Test
    @DisplayName("GET /{accountId} - transaction service failure still returns 200 with transactionsLoadFailed=true")
    void getAccountDetail_transactionServiceDown_returnsOkWithFailedFlag() throws Exception {
        AccountDetailResponse responseWithFailedTransactions = sampleDetailResponse;
        responseWithFailedTransactions.setTransactionsLoadFailed(true);
        responseWithFailedTransactions.setRecentTransactions(List.of());

        when(jwtUtil.extractUserId("Bearer valid-token")).thenReturn(1L);
        when(accountDetailService.getAccountDetailById(1L, 1L)).thenReturn(responseWithFailedTransactions);

        mockMvc.perform(get("/api/v1/accounts/1")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.transactionsLoadFailed").value(true))
                .andExpect(jsonPath("$.data.recentTransactions").isEmpty());
    }
}
