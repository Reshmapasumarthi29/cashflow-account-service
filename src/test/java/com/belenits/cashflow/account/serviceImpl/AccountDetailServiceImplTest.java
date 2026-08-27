package com.belenits.cashflow.account.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.belenits.cashflow.account.client.TransactionServiceClient;
import com.belenits.cashflow.account.dto.response.AccountDetailResponse;
import com.belenits.cashflow.account.dto.response.BankDetailsResponse;
import com.belenits.cashflow.account.dto.response.CashWalletDetailsResponse;
import com.belenits.cashflow.account.dto.response.CreditCardDetailsResponse;
import com.belenits.cashflow.account.dto.response.InvestmentDetailsResponse;
import com.belenits.cashflow.account.dto.response.LoanDetailsResponse;
import com.belenits.cashflow.account.dto.response.TransactionListItemResponse;
import com.belenits.cashflow.account.dto.response.TransactionListPageResponse;
import com.belenits.cashflow.account.dto.response.TransactionListResponse;
import com.belenits.cashflow.account.entity.Account;
import com.belenits.cashflow.account.entity.AccountType;
import com.belenits.cashflow.account.entity.BankAccountDetails;
import com.belenits.cashflow.account.entity.CashWalletDetails;
import com.belenits.cashflow.account.entity.CreditCardDetails;
import com.belenits.cashflow.account.entity.InvestmentAccountDetails;
import com.belenits.cashflow.account.entity.LoanAccountDetails;
import com.belenits.cashflow.account.exception.AccountAccessDeniedException;
import com.belenits.cashflow.account.exception.AccountNotFoundException;
import com.belenits.cashflow.account.mapper.AccountDetailMapper;
import com.belenits.cashflow.account.repository.AccountRepository;
import com.belenits.cashflow.account.repository.BankAccountDetailsRepository;
import com.belenits.cashflow.account.repository.CashWalletDetailsRepository;
import com.belenits.cashflow.account.repository.CreditCardDetailsRepository;
import com.belenits.cashflow.account.repository.InvestmentAccountDetailsRepository;
import com.belenits.cashflow.account.repository.LoanAccountDetailsRepository;

@ExtendWith(MockitoExtension.class)
class AccountDetailServiceImplTest {

    @Mock
    private AccountDetailMapper accountDetailMapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BankAccountDetailsRepository bankAccountDetailsRepository;
    @Mock
    private CreditCardDetailsRepository creditCardDetailsRepository;
    @Mock
    private CashWalletDetailsRepository cashWalletDetailsRepository;
    @Mock
    private InvestmentAccountDetailsRepository investmentAccountDetailsRepository;
    @Mock
    private LoanAccountDetailsRepository loanAccountDetailsRepository;
    @Mock
    private TransactionServiceClient transactionServiceClient;

    @InjectMocks
    private AccountDetailServiceImpl service;

    @Test
    @DisplayName("getAccountDetailById: throws AccountNotFoundException when account does not exist")
    void getAccountDetailById_accountNotFound() {
        Long accountId = 10L;
        Long userId = 99L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(
                AccountNotFoundException.class,
                () -> service.getAccountDetailById(accountId, userId));

        assertEquals("Account with id 10 not found", ex.getMessage());
        verify(accountRepository).findById(accountId);
        verify(accountDetailMapper, never()).toAccountDetailResponse(org.mockito.ArgumentMatchers.any());
        verify(transactionServiceClient, never()).getTransactions(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("getAccountDetailById: throws AccountAccessDeniedException when user does not own account")
    void getAccountDetailById_accessDenied() {
        Long accountId = 1L;
        Long ownerId = 101L;
        Long requesterId = 202L;
        Account account = baseAccount(accountId, ownerId, "BANK");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountAccessDeniedException ex = assertThrows(
                AccountAccessDeniedException.class,
                () -> service.getAccountDetailById(accountId, requesterId));

        assertEquals("User with id 202 does not have access to account with id 1", ex.getMessage());
        verify(accountRepository).findById(accountId);
        verify(accountDetailMapper, never()).toAccountDetailResponse(org.mockito.ArgumentMatchers.any());
        verify(transactionServiceClient, never()).getTransactions(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("BANK: returns mapped response with bank details and recent transactions")
    void getAccountDetailById_bank_success() {
        Long accountId = 1L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "BANK");
        AccountDetailResponse mapped = new AccountDetailResponse();

        BankAccountDetails bankDetails = new BankAccountDetails();
        bankDetails.setAccountNumberLast4("1234");
        bankDetails.setIfscCode("HDFC0001234");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(bankAccountDetailsRepository.findById(accountId)).thenReturn(Optional.of(bankDetails));
        when(accountDetailMapper.toAccountDetailResponse(account)).thenReturn(mapped);
        when(transactionServiceClient.getTransactions(userId, accountId, 0, 5)).thenReturn(txResponse(
                txItem(LocalDate.of(2026, 8, 1), "Coffee", new BigDecimal("120.00")),
                txItem(LocalDate.of(2026, 8, 2), "Fuel", new BigDecimal("1000.00"))));

        AccountDetailResponse result = service.getAccountDetailById(accountId, userId);

        assertInstanceOf(BankDetailsResponse.class, result.getTypeSpecificDetails());
        BankDetailsResponse details = (BankDetailsResponse) result.getTypeSpecificDetails();
        assertEquals("1234", details.getAccountNumberLast4());
        assertEquals("HDFC0001234", details.getIfscCode());

        assertFalse(result.isTransactionsLoadFailed());
        assertEquals(2, result.getRecentTransactions().size());
        assertEquals("Coffee", result.getRecentTransactions().get(0).getDescription());
        assertEquals(new BigDecimal("120.00"), result.getRecentTransactions().get(0).getAmount());

        verify(transactionServiceClient).getTransactions(userId, accountId, 0, 5);
    }

    @Test
    @DisplayName("CREDIT_CARD: returns mapped response with credit card details")
    void getAccountDetailById_creditCard_success() {
        Long accountId = 2L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "CREDIT_CARD");
        AccountDetailResponse mapped = new AccountDetailResponse();

        CreditCardDetails card = new CreditCardDetails();
        card.setCardNumberLast4("7788");
        card.setCreditLimit(new BigDecimal("500000.00"));
        card.setBillingCycleDay(5);
        card.setPaymentDueDay(20);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(creditCardDetailsRepository.findById(accountId)).thenReturn(Optional.of(card));
        when(accountDetailMapper.toAccountDetailResponse(account)).thenReturn(mapped);
        when(transactionServiceClient.getTransactions(userId, accountId, 0, 5)).thenReturn(txResponse());

        AccountDetailResponse result = service.getAccountDetailById(accountId, userId);

        assertInstanceOf(CreditCardDetailsResponse.class, result.getTypeSpecificDetails());
        CreditCardDetailsResponse details = (CreditCardDetailsResponse) result.getTypeSpecificDetails();
        assertEquals("7788", details.getCardNumberLast4());
        assertEquals(new BigDecimal("500000.00"), details.getCreditLimit());
        assertEquals(5, details.getBillingCycleDay());
        assertEquals(20, details.getPaymentDueDay());
        assertFalse(result.isTransactionsLoadFailed());
    }

    @Test
    @DisplayName("CASH_WALLET: returns mapped response with wallet details")
    void getAccountDetailById_cashWallet_success() {
        Long accountId = 3L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "CASH_WALLET");
        AccountDetailResponse mapped = new AccountDetailResponse();

        CashWalletDetails wallet = new CashWalletDetails();
        wallet.setProviderName("Paytm");
        wallet.setWalletIdentifier("paytm-user-01");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(cashWalletDetailsRepository.findById(accountId)).thenReturn(Optional.of(wallet));
        when(accountDetailMapper.toAccountDetailResponse(account)).thenReturn(mapped);
        when(transactionServiceClient.getTransactions(userId, accountId, 0, 5)).thenReturn(txResponse());

        AccountDetailResponse result = service.getAccountDetailById(accountId, userId);

        assertInstanceOf(CashWalletDetailsResponse.class, result.getTypeSpecificDetails());
        CashWalletDetailsResponse details = (CashWalletDetailsResponse) result.getTypeSpecificDetails();
        assertEquals("Paytm", details.getProviderName());
        assertEquals("paytm-user-01", details.getWalletIdentifier());
        assertFalse(result.isTransactionsLoadFailed());
    }

    @Test
    @DisplayName("INVESTMENT: returns mapped response with investment details")
    void getAccountDetailById_investment_success() {
        Long accountId = 4L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "INVESTMENT");
        AccountDetailResponse mapped = new AccountDetailResponse();

        InvestmentAccountDetails investment = new InvestmentAccountDetails();
        investment.setPlatformName("Groww");
        investment.setInvestedAmount(new BigDecimal("250000.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(investmentAccountDetailsRepository.findById(accountId)).thenReturn(Optional.of(investment));
        when(accountDetailMapper.toAccountDetailResponse(account)).thenReturn(mapped);
        when(transactionServiceClient.getTransactions(userId, accountId, 0, 5)).thenReturn(txResponse());

        AccountDetailResponse result = service.getAccountDetailById(accountId, userId);

        assertInstanceOf(InvestmentDetailsResponse.class, result.getTypeSpecificDetails());
        InvestmentDetailsResponse details = (InvestmentDetailsResponse) result.getTypeSpecificDetails();
        assertEquals("Groww", details.getPlatformName());
        assertEquals(new BigDecimal("250000.00"), details.getInvestedAmount());
        assertFalse(result.isTransactionsLoadFailed());
    }

    @Test
    @DisplayName("LOAN: returns mapped response with loan details")
    void getAccountDetailById_loan_success() {
        Long accountId = 5L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "LOAN");
        AccountDetailResponse mapped = new AccountDetailResponse();

        LoanAccountDetails loan = new LoanAccountDetails();
        loan.setLoanAccountNumberLast4("4455");
        loan.setOriginalLoanAmount(new BigDecimal("1500000.00"));
        loan.setInterestRate(new BigDecimal("8.5000"));
        loan.setEmiAmount(new BigDecimal("31000.00"));
        loan.setEmiDueDay(10);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(loanAccountDetailsRepository.findById(accountId)).thenReturn(Optional.of(loan));
        when(accountDetailMapper.toAccountDetailResponse(account)).thenReturn(mapped);
        when(transactionServiceClient.getTransactions(userId, accountId, 0, 5)).thenReturn(txResponse());

        AccountDetailResponse result = service.getAccountDetailById(accountId, userId);

        assertInstanceOf(LoanDetailsResponse.class, result.getTypeSpecificDetails());
        LoanDetailsResponse details = (LoanDetailsResponse) result.getTypeSpecificDetails();
        assertEquals("4455", details.getLoanAccountNumberLast4());
        assertEquals(new BigDecimal("1500000.00"), details.getLoanAmount());
        assertEquals(new BigDecimal("8.5000"), details.getInterestRate());
        assertEquals(new BigDecimal("31000.00"), details.getEmiAmount());
        assertEquals(10, details.getEmiDueDay());
        assertFalse(result.isTransactionsLoadFailed());
    }

    @Test
    @DisplayName("Unknown type code: throws IllegalStateException")
    void getAccountDetailById_unknownType_throwsIllegalStateException() {
        Long accountId = 100L;
        Long userId = 200L;
        Account account = baseAccount(accountId, userId, "SOMETHING_UNKNOWN");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.getAccountDetailById(accountId, userId));

        assertEquals("Unknown account type code: SOMETHING_UNKNOWN", ex.getMessage());
        verify(transactionServiceClient, never()).getTransactions(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("BANK details missing: throws IllegalStateException")
    void getAccountDetailById_bankDetailsMissing_throwsIllegalStateException() {
        Long accountId = 11L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "BANK");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(bankAccountDetailsRepository.findById(accountId)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.getAccountDetailById(accountId, userId));

        assertEquals("Bank details missing for account 11", ex.getMessage());
        verify(transactionServiceClient, never()).getTransactions(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("CREDIT_CARD details missing: throws IllegalStateException")
    void getAccountDetailById_creditCardDetailsMissing_throwsIllegalStateException() {
        Long accountId = 12L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "CREDIT_CARD");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(creditCardDetailsRepository.findById(accountId)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.getAccountDetailById(accountId, userId));

        assertEquals("Bank details missing for account 12", ex.getMessage());
    }

    @Test
    @DisplayName("CASH_WALLET details missing: throws IllegalStateException")
    void getAccountDetailById_cashWalletDetailsMissing_throwsIllegalStateException() {
        Long accountId = 13L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "CASH_WALLET");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(cashWalletDetailsRepository.findById(accountId)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.getAccountDetailById(accountId, userId));

        assertEquals("Bank details missing for account 13", ex.getMessage());
    }

    @Test
    @DisplayName("INVESTMENT details missing: throws IllegalStateException")
    void getAccountDetailById_investmentDetailsMissing_throwsIllegalStateException() {
        Long accountId = 14L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "INVESTMENT");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(investmentAccountDetailsRepository.findById(accountId)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.getAccountDetailById(accountId, userId));

        assertEquals("Bank details missing for account 14", ex.getMessage());
    }

    @Test
    @DisplayName("LOAN details missing: throws IllegalStateException")
    void getAccountDetailById_loanDetailsMissing_throwsIllegalStateException() {
        Long accountId = 15L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "LOAN");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(loanAccountDetailsRepository.findById(accountId)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.getAccountDetailById(accountId, userId));

        assertEquals("Bank details missing for account 15", ex.getMessage());
    }

    @Test
    @DisplayName("Transaction service exception: returns response with empty transactions and failed flag true")
    void getAccountDetailById_transactionServiceThrows_returnsFallback() {
        Long accountId = 20L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "BANK");
        AccountDetailResponse mapped = new AccountDetailResponse();

        BankAccountDetails bank = new BankAccountDetails();
        bank.setAccountNumberLast4("1111");
        bank.setIfscCode("TEST0001");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(bankAccountDetailsRepository.findById(accountId)).thenReturn(Optional.of(bank));
        when(accountDetailMapper.toAccountDetailResponse(account)).thenReturn(mapped);
        when(transactionServiceClient.getTransactions(userId, accountId, 0, 5))
                .thenThrow(new RuntimeException("transaction service down"));

        AccountDetailResponse result = service.getAccountDetailById(accountId, userId);

        assertTrue(result.isTransactionsLoadFailed());
        assertEquals(0, result.getRecentTransactions().size());
    }

    @Test
    @DisplayName("Null transaction response/data/content: handled and marked as failed")
    void getAccountDetailById_transactionResponseNullOrInvalid_returnsFallback() {
        Long accountId = 21L;
        Long userId = 7L;
        Account account = baseAccount(accountId, userId, "BANK");
        AccountDetailResponse mapped = new AccountDetailResponse();

        BankAccountDetails bank = new BankAccountDetails();
        bank.setAccountNumberLast4("9999");
        bank.setIfscCode("NULL0001");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(bankAccountDetailsRepository.findById(accountId)).thenReturn(Optional.of(bank));
        when(accountDetailMapper.toAccountDetailResponse(account)).thenReturn(mapped);

        TransactionListResponse invalid = new TransactionListResponse(); // data remains null
        when(transactionServiceClient.getTransactions(userId, accountId, 0, 5)).thenReturn(invalid);

        AccountDetailResponse result = service.getAccountDetailById(accountId, userId);

        assertTrue(result.isTransactionsLoadFailed());
        assertEquals(0, result.getRecentTransactions().size());
    }

    private Account baseAccount(Long accountId, Long userId, String typeCode) {
        AccountType type = new AccountType();
        type.setTypeCode(typeCode);
        type.setTypeName(typeCode + "_NAME");

        Account account = new Account();
        account.setAccountId(accountId);
        account.setUserId(userId);
        account.setAccountType(type);
        account.setAccountName("Test Account");
        account.setCurrencyCode("INR");
        account.setCurrentBalance(new BigDecimal("1000.00"));
        account.setAvailableBalance(new BigDecimal("900.00"));
        return account;
    }

    private TransactionListResponse txResponse(TransactionListItemResponse... items) {
        TransactionListPageResponse page = new TransactionListPageResponse();
        page.setContent(List.of(items));
        page.setPage(0);
        page.setSize(5);
        page.setTotalElements(items.length);
        page.setTotalPages(1);

        TransactionListResponse response = new TransactionListResponse();
        response.setSuccess(true);
        response.setData(page);
        response.setCorrelationId("corr-1");
        return response;
    }

    private TransactionListItemResponse txItem(LocalDate date, String description, BigDecimal amount) {
        TransactionListItemResponse item = new TransactionListItemResponse();
        item.setDate(date);
        item.setDescription(description);
        item.setAmount(amount);
        return item;
    }
}
