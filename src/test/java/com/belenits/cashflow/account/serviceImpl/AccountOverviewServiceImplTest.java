package com.belenits.cashflow.account.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.belenits.cashflow.account.dto.response.AccountOverviewResponse;
import com.belenits.cashflow.account.dto.response.AccountSummaryResponse;
import com.belenits.cashflow.account.dto.response.AccountTypeSummaryResponse;
import com.belenits.cashflow.account.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountOverviewServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountOverviewServiceImpl service;

    @Test
    @DisplayName("getAccountOverview: null totalBalance defaults to ZERO and percentages stay 0")
    void getAccountOverview_totalBalanceNull_defaultsToZero() {
        Long userId = 1L;
        List<AccountTypeSummaryResponse> typeSummaries = new ArrayList<>();
        typeSummaries.add(typeSummary(10L, "Bank", new BigDecimal("500.00")));
        List<AccountSummaryResponse> accountSummaries = List.of();

        when(accountRepository.getTotalBalance(userId)).thenReturn(null);
        when(accountRepository.getTotalAccounts(userId)).thenReturn(1);
        when(accountRepository.getAccountTypeSummaries(userId)).thenReturn(typeSummaries);
        when(accountRepository.getAccountSummaries(userId)).thenReturn(accountSummaries);

        AccountOverviewResponse response = service.getAccountOverview(userId);

        assertEquals(BigDecimal.ZERO, response.getTotalBalance());
        assertEquals(1, response.getTotalAccounts());
        assertEquals(1, response.getAccountsByType().size());
        assertEquals(BigDecimal.ZERO, response.getAccountsByType().get(0).getPercentage());
        assertEquals(accountSummaries, response.getAccounts());

        verify(accountRepository).getTotalBalance(userId);
        verify(accountRepository).getTotalAccounts(userId);
        verify(accountRepository).getAccountTypeSummaries(userId);
        verify(accountRepository).getAccountSummaries(userId);
    }

    @Test
    @DisplayName("getAccountOverview: computes percentages with HALF_UP and sorts by totalBalance desc")
    void getAccountOverview_positiveTotal_computesPercentageAndSorts() {
        Long userId = 2L;

        // Intentionally unsorted input
        AccountTypeSummaryResponse t1 = typeSummary(1L, "Type-1", new BigDecimal("1"));
        AccountTypeSummaryResponse t2 = typeSummary(2L, "Type-2", new BigDecimal("2"));
        List<AccountTypeSummaryResponse> typeSummaries = new ArrayList<>(List.of(t1, t2));

        List<AccountSummaryResponse> accountSummaries = List.of(
                new AccountSummaryResponse(11L, "A1", "Type-1", new BigDecimal("1"), "1111", null),
                new AccountSummaryResponse(22L, "A2", "Type-2", new BigDecimal("2"), "2222", null));

        when(accountRepository.getTotalBalance(userId)).thenReturn(new BigDecimal("3"));
        when(accountRepository.getTotalAccounts(userId)).thenReturn(2);
        when(accountRepository.getAccountTypeSummaries(userId)).thenReturn(typeSummaries);
        when(accountRepository.getAccountSummaries(userId)).thenReturn(accountSummaries);

        AccountOverviewResponse response = service.getAccountOverview(userId);

        // 1/3*100 = 33.33 -> 33 (HALF_UP, scale 0)
        // 2/3*100 = 66.66 -> 67
        assertEquals(new BigDecimal("33"), t1.getPercentage());
        assertEquals(new BigDecimal("67"), t2.getPercentage());

        // Sorted descending by totalBalance => t2 then t1
        assertEquals("Type-2", response.getAccountsByType().get(0).getAccountTypeName());
        assertEquals("Type-1", response.getAccountsByType().get(1).getAccountTypeName());
        assertEquals(accountSummaries, response.getAccounts());
    }

    @Test
    @DisplayName("getAccountOverview: when totalBalance is ZERO percentages remain ZERO")
    void getAccountOverview_totalZero_percentagesRemainZero() {
        Long userId = 3L;

        AccountTypeSummaryResponse t1 = typeSummary(1L, "Type-A", new BigDecimal("100"));
        AccountTypeSummaryResponse t2 = typeSummary(2L, "Type-B", new BigDecimal("50"));
        List<AccountTypeSummaryResponse> typeSummaries = new ArrayList<>(List.of(t1, t2));

        when(accountRepository.getTotalBalance(userId)).thenReturn(BigDecimal.ZERO);
        when(accountRepository.getTotalAccounts(userId)).thenReturn(2);
        when(accountRepository.getAccountTypeSummaries(userId)).thenReturn(typeSummaries);
        when(accountRepository.getAccountSummaries(userId)).thenReturn(List.of());

        AccountOverviewResponse response = service.getAccountOverview(userId);

        assertEquals(BigDecimal.ZERO, response.getTotalBalance());
        assertEquals(BigDecimal.ZERO, t1.getPercentage());
        assertEquals(BigDecimal.ZERO, t2.getPercentage());
    }

    @Test
    @DisplayName("getAccountOverview: empty type summaries and accounts returns valid response")
    void getAccountOverview_emptyLists() {
        Long userId = 4L;

        when(accountRepository.getTotalBalance(userId)).thenReturn(new BigDecimal("0"));
        when(accountRepository.getTotalAccounts(userId)).thenReturn(0);
        when(accountRepository.getAccountTypeSummaries(userId)).thenReturn(new ArrayList<>());
        when(accountRepository.getAccountSummaries(userId)).thenReturn(new ArrayList<>());

        AccountOverviewResponse response = service.getAccountOverview(userId);

        assertEquals(new BigDecimal("0"), response.getTotalBalance());
        assertEquals(0, response.getTotalAccounts());
        assertEquals(0, response.getAccountsByType().size());
        assertEquals(0, response.getAccounts().size());
    }

    @Test
    @DisplayName("getAccountOverview: null account summaries is passed through as null in response")
    void getAccountOverview_nullAccountSummaries() {
        Long userId = 5L;

        List<AccountTypeSummaryResponse> typeSummaries = new ArrayList<>();
        typeSummaries.add(typeSummary(1L, "Type-X", new BigDecimal("10")));

        when(accountRepository.getTotalBalance(userId)).thenReturn(new BigDecimal("10"));
        when(accountRepository.getTotalAccounts(userId)).thenReturn(1);
        when(accountRepository.getAccountTypeSummaries(userId)).thenReturn(typeSummaries);
        when(accountRepository.getAccountSummaries(userId)).thenReturn(null);

        AccountOverviewResponse response = service.getAccountOverview(userId);

        assertNull(response.getAccounts());
        assertEquals(1, response.getAccountsByType().size());
    }

    @Test
    @DisplayName("getAccountOverview: null accountTypeSummaries throws NullPointerException")
    void getAccountOverview_nullTypeSummaries_throwsNpe() {
        Long userId = 6L;

        when(accountRepository.getTotalBalance(userId)).thenReturn(new BigDecimal("10"));
        when(accountRepository.getTotalAccounts(userId)).thenReturn(1);
        when(accountRepository.getAccountTypeSummaries(userId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> service.getAccountOverview(userId));
    }

    private AccountTypeSummaryResponse typeSummary(Long id, String name, BigDecimal totalBalance) {
        AccountTypeSummaryResponse summary = new AccountTypeSummaryResponse();
        summary.setAccountTypeId(id);
        summary.setAccountTypeName(name);
        summary.setTotalBalance(totalBalance);
        return summary;
    }
}

