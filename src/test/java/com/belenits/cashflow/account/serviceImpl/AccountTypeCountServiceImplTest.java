package com.belenits.cashflow.account.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.belenits.cashflow.account.dto.response.AccountTypeCountResponse;
import com.belenits.cashflow.account.repository.AccountTypeRepository;

@ExtendWith(MockitoExtension.class)
class AccountTypeCountServiceImplTest {

    @Mock
    private AccountTypeRepository accountTypeRepository;

    @InjectMocks
    private AccountTypeCountServiceImpl service;

    @Test
    @DisplayName("getAccountTypeCounts: returns repository data for valid userId")
    void getAccountTypeCounts_validUserId_returnsData() {
        Long userId = 1L;
        List<AccountTypeCountResponse> expected = List.of(
                new AccountTypeCountResponse(1L, "BANK", "Bank Account", "Savings/Current", 3L),
                new AccountTypeCountResponse(2L, "CREDIT_CARD", "Credit Card", "Card accounts", 1L));

        when(accountTypeRepository.getAccountTypeCounts(userId)).thenReturn(expected);

        List<AccountTypeCountResponse> actual = service.getAccountTypeCounts(userId);

        assertSame(expected, actual); // service should return same list object from repository
        assertEquals(2, actual.size());
        assertEquals("BANK", actual.get(0).getTypeCode());
        assertEquals(3L, actual.get(0).getAccountCount());
        verify(accountTypeRepository).getAccountTypeCounts(userId);
    }

    @Test
    @DisplayName("getAccountTypeCounts: returns empty list when repository returns empty list")
    void getAccountTypeCounts_emptyList_returnsEmptyList() {
        Long userId = 2L;
        List<AccountTypeCountResponse> expected = List.of();

        when(accountTypeRepository.getAccountTypeCounts(userId)).thenReturn(expected);

        List<AccountTypeCountResponse> actual = service.getAccountTypeCounts(userId);

        assertSame(expected, actual);
        assertEquals(0, actual.size());
        verify(accountTypeRepository).getAccountTypeCounts(userId);
    }

    @Test
    @DisplayName("getAccountTypeCounts: returns null when repository returns null")
    void getAccountTypeCounts_repositoryReturnsNull_returnsNull() {
        Long userId = 3L;
        when(accountTypeRepository.getAccountTypeCounts(userId)).thenReturn(null);

        List<AccountTypeCountResponse> actual = service.getAccountTypeCounts(userId);

        assertNull(actual);
        verify(accountTypeRepository).getAccountTypeCounts(userId);
    }

    @Test
    @DisplayName("getAccountTypeCounts: propagates exception from repository")
    void getAccountTypeCounts_repositoryThrows_propagatesException() {
        Long userId = 4L;
        RuntimeException expected = new RuntimeException("DB unavailable");

        when(accountTypeRepository.getAccountTypeCounts(userId)).thenThrow(expected);

        RuntimeException actual = assertThrows(
                RuntimeException.class,
                () -> service.getAccountTypeCounts(userId));

        assertSame(expected, actual);
        verify(accountTypeRepository).getAccountTypeCounts(userId);
    }

    @Test
    @DisplayName("getAccountTypeCounts: passes null userId to repository as-is")
    void getAccountTypeCounts_nullUserId_delegatesToRepository() {
        List<AccountTypeCountResponse> expected = List.of(
                new AccountTypeCountResponse(1L, "BANK", "Bank Account", "Savings/Current", 0L));

        when(accountTypeRepository.getAccountTypeCounts(null)).thenReturn(expected);

        List<AccountTypeCountResponse> actual = service.getAccountTypeCounts(null);

        assertSame(expected, actual);
        verify(accountTypeRepository).getAccountTypeCounts(null);
    }
}
