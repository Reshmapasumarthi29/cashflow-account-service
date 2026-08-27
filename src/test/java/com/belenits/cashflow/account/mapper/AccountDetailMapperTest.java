package com.belenits.cashflow.account.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.belenits.cashflow.account.dto.response.AccountDetailResponse;
import com.belenits.cashflow.account.entity.Account;
import com.belenits.cashflow.account.entity.AccountStatus;
import com.belenits.cashflow.account.entity.AccountType;
import com.belenits.cashflow.account.entity.FinancialInstitution;

class AccountDetailMapperTest {

    private final AccountDetailMapper mapper = new AccountDetailMapper();

    @Test
    @DisplayName("toAccountDetailResponse maps all fields when institution is present")
    void toAccountDetailResponse_withInstitution_mapsAllFields() {
        AccountType accountType = new AccountType();
        accountType.setTypeCode("BANK");
        accountType.setTypeName("Bank Account");

        FinancialInstitution institution = new FinancialInstitution();
        institution.setInstitutionName("HDFC Bank");

        Account account = new Account();
        account.setAccountId(1L);
        account.setAccountName("Main Savings");
        account.setAccountType(accountType);
        account.setFinancialInstitution(institution);
        account.setCurrencyCode("INR");
        account.setCurrentBalance(new BigDecimal("12500.50"));
        account.setAvailableBalance(new BigDecimal("12000.00"));
        account.setStatus(AccountStatus.ACTIVE);

        AccountDetailResponse response = mapper.toAccountDetailResponse(account);

        assertEquals(1L, response.getAccountId());
        assertEquals("Main Savings", response.getAccountName());
        assertEquals("BANK", response.getTypeCode());
        assertEquals("Bank Account", response.getAccountTypeName());
        assertEquals("HDFC Bank", response.getInstitutionName());
        assertEquals("INR", response.getCurrencyCode());
        assertEquals(new BigDecimal("12500.50"), response.getCurrentBalance());
        assertEquals(new BigDecimal("12000.00"), response.getAvailableBalance());
        assertEquals(AccountStatus.ACTIVE, response.getStatus());
    }

    @Test
    @DisplayName("toAccountDetailResponse sets institutionName null when institution is absent")
    void toAccountDetailResponse_withoutInstitution_setsInstitutionNameNull() {
        AccountType accountType = new AccountType();
        accountType.setTypeCode("CASH_WALLET");
        accountType.setTypeName("Cash Wallet");

        Account account = new Account();
        account.setAccountId(2L);
        account.setAccountName("Wallet");
        account.setAccountType(accountType);
        account.setFinancialInstitution(null);
        account.setCurrencyCode("INR");
        account.setCurrentBalance(new BigDecimal("200.00"));
        account.setAvailableBalance(new BigDecimal("200.00"));
        account.setStatus(AccountStatus.ACTIVE);

        AccountDetailResponse response = mapper.toAccountDetailResponse(account);

        assertEquals(2L, response.getAccountId());
        assertEquals("Wallet", response.getAccountName());
        assertEquals("CASH_WALLET", response.getTypeCode());
        assertEquals("Cash Wallet", response.getAccountTypeName());
        assertNull(response.getInstitutionName());
    }
}

