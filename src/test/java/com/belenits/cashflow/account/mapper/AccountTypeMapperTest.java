package com.belenits.cashflow.account.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.belenits.cashflow.account.dto.response.AccountTypeResponse;
import com.belenits.cashflow.account.entity.AccountType;

class AccountTypeMapperTest {

    private final AccountTypeMapper mapper = new AccountTypeMapper();

    @Test
    @DisplayName("toAccountTypeResponse maps account type fields")
    void toAccountTypeResponse_mapsFields() {
        AccountType accountType = new AccountType();
        accountType.setAccountTypeId(10L);
        accountType.setTypeCode("LOAN");
        accountType.setTypeName("Loan Account");
        accountType.setDescription("Loan based accounts");

        AccountTypeResponse response = mapper.toAccountTypeResponse(accountType);

        assertEquals(10L, response.getAccountTypeId());
        assertEquals("LOAN", response.getTypeCode());
        assertEquals("Loan Account", response.getTypeName());
        assertEquals("Loan based accounts", response.getDescription());
    }
}

