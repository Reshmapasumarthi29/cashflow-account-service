package com.belenits.cashflow.account.serviceImpl;

import com.belenits.cashflow.account.repository.BankAccountDetailsRepository;
import com.belenits.cashflow.account.repository.CashWalletDetailsRepository;
import com.belenits.cashflow.account.repository.CreditCardDetailsRepository;
import com.belenits.cashflow.account.repository.InvestmentAccountDetailsRepository;
import com.belenits.cashflow.account.repository.LoanAccountDetailsRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.belenits.cashflow.account.client.TransactionServiceClient;
import com.belenits.cashflow.account.dto.response.AccountDetailResponse;
import com.belenits.cashflow.account.dto.response.AccountTypeDetails;
import com.belenits.cashflow.account.dto.response.BankDetailsResponse;
import com.belenits.cashflow.account.dto.response.CashWalletDetailsResponse;
import com.belenits.cashflow.account.dto.response.CreditCardDetailsResponse;
import com.belenits.cashflow.account.dto.response.InvestmentDetailsResponse;
import com.belenits.cashflow.account.dto.response.LoanDetailsResponse;
import com.belenits.cashflow.account.dto.response.RecentTransactionResponse;
import com.belenits.cashflow.account.dto.response.TransactionListItemResponse;
import com.belenits.cashflow.account.dto.response.TransactionListResponse;
import com.belenits.cashflow.account.entity.Account;
import com.belenits.cashflow.account.entity.BankAccountDetails;
import com.belenits.cashflow.account.entity.CashWalletDetails;
import com.belenits.cashflow.account.entity.CreditCardDetails;
import com.belenits.cashflow.account.entity.InvestmentAccountDetails;
import com.belenits.cashflow.account.entity.LoanAccountDetails;
import com.belenits.cashflow.account.exception.AccountAccessDeniedException;
import com.belenits.cashflow.account.exception.AccountNotFoundException;
import com.belenits.cashflow.account.mapper.AccountDetailMapper;
import com.belenits.cashflow.account.repository.AccountRepository;
import com.belenits.cashflow.account.service.AccountDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDetailServiceImpl implements AccountDetailService {

	private final AccountDetailMapper accountDetailMapper;

	private final AccountRepository accountRepository;

	private final BankAccountDetailsRepository bankAccountDetailsRepository;

	private final CreditCardDetailsRepository creditCardDetailsRepository;

	private final CashWalletDetailsRepository cashWalletDetailsRepository;

	private final InvestmentAccountDetailsRepository investmentAccountDetailsRepository;

	private final LoanAccountDetailsRepository loanAccountDetailsRepository;
	
	private final TransactionServiceClient transactionServiceClient;

	@Override
	public AccountDetailResponse getAccountDetailById(Long accountId, Long userId) {
		
		 log.info("Fetching account detail for accountId={}, userId={}", accountId, userId);

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> {
					log.warn("Account not found for accountId={}, userId={}", accountId, userId);
				return	new AccountNotFoundException("Account with id " + accountId + " not found");
				});
		if (!account.getUserId().equals(userId)) {

			log.warn("Access denied for userId={} on accountId={}", userId, accountId);
			throw new AccountAccessDeniedException(
					"User with id " + userId + " does not have access to account with id " + accountId);
		}

		AccountTypeDetails typeSpecificDetails = resolveTypeSpecificDetails(account);
		
	      log.debug("Resolved type-specific details for accountId={}, typeCode={}",
	                accountId, account.getAccountType().getTypeCode());
		
		List<RecentTransactionResponse> recentTransactions = getRecentTransactions(accountId, userId, 0, 5);

        log.debug("Recent transactions load status for accountId={}, success={}",
                accountId, recentTransactions != null);
		
		AccountDetailResponse response = accountDetailMapper.toAccountDetailResponse(account);
		response.setTypeSpecificDetails(typeSpecificDetails);
		response.setRecentTransactions(recentTransactions!=null ? recentTransactions : List.of());
		response.setTransactionsLoadFailed(recentTransactions==null);
		
	    log.info("Account detail response prepared for accountId={}, userId={}", accountId, userId);
	    
		return response;

	}

	private AccountTypeDetails resolveTypeSpecificDetails(Account account) {

		Long accountId = account.getAccountId();
		String typeCode = account.getAccountType().getTypeCode();
		
		 log.debug("Resolving type-specific details for accountId={}, typeCode={}", accountId, typeCode);

		switch (typeCode) {

		case "BANK" -> {
			BankAccountDetails bankDetails = bankAccountDetailsRepository.findById(accountId)
					.orElseThrow(() -> new IllegalStateException("Bank details missing for account " + accountId));
			BankDetailsResponse response = new BankDetailsResponse();
			response.setAccountNumberLast4(bankDetails.getAccountNumberLast4());
			response.setIfscCode(bankDetails.getIfscCode());
			return response;
		}

		case "CREDIT_CARD" -> {
			CreditCardDetails creditDetails = creditCardDetailsRepository.findById(accountId)
					.orElseThrow(() -> new IllegalStateException("Bank details missing for account " + accountId));
			CreditCardDetailsResponse response = new CreditCardDetailsResponse();
			response.setCardNumberLast4(creditDetails.getCardNumberLast4());
			response.setCreditLimit(creditDetails.getCreditLimit());
			response.setBillingCycleDay(creditDetails.getBillingCycleDay());
			response.setPaymentDueDay(creditDetails.getPaymentDueDay());
			return response;
		}

		case "CASH_WALLET" -> {
			CashWalletDetails cashWalletDetails = cashWalletDetailsRepository.findById(accountId)
					.orElseThrow(() -> new IllegalStateException("Bank details missing for account " + accountId));

			CashWalletDetailsResponse response = new CashWalletDetailsResponse();
			response.setProviderName(cashWalletDetails.getProviderName());
			response.setWalletIdentifier(cashWalletDetails.getWalletIdentifier());
			return response;
		}

		case "INVESTMENT" -> {
			InvestmentAccountDetails investmentDetails = investmentAccountDetailsRepository.findById(accountId)
					.orElseThrow(() -> new IllegalStateException("Bank details missing for account " + accountId));
			InvestmentDetailsResponse response = new InvestmentDetailsResponse();
			response.setPlatformName(investmentDetails.getPlatformName());
			response.setInvestedAmount(investmentDetails.getInvestedAmount());
			return response;
		}

		case "LOAN" -> {
			LoanAccountDetails loanDetails = loanAccountDetailsRepository.findById(accountId)
					.orElseThrow(() -> new IllegalStateException("Bank details missing for account " + accountId));
			LoanDetailsResponse response = new LoanDetailsResponse();
			response.setLoanAccountNumberLast4(loanDetails.getLoanAccountNumberLast4());
			response.setLoanAmount(loanDetails.getOriginalLoanAmount());
			response.setInterestRate(loanDetails.getInterestRate());
			response.setEmiAmount(loanDetails.getEmiAmount());
			response.setEmiDueDay(loanDetails.getEmiDueDay());
			return response;
		}

		default ->{ 
			
	           log.error("Unknown account type code '{}' for accountId={}", typeCode, accountId);
			throw new IllegalStateException("Unknown account type code: " + typeCode);
		}
		}
	}

	private List<RecentTransactionResponse> getRecentTransactions(Long accountId, Long userId, int page, int size){
		
		// This method would typically call the TransactionServiceClient to fetch recent transactions.
        log.debug("Fetching recent transactions for accountId={}, userId={}, page={}, size={}",
                accountId, userId, page, size);
        
		try {
			TransactionListResponse transactionListResponse = transactionServiceClient.getTransactions(userId, accountId, page, size);
			
			return transactionListResponse.getData().getContent().stream()
					                              .map(item->toRecentTransactionResponse(item))
					                              .toList();
		}
		catch (Exception e) {
			
			 log.warn("Failed to load recent transactions for accountId={}, userId={}", accountId, userId, e);

			 return null;   // signals "failed to load" — handled upstream, doesn't break the whole response
		}
		
		
		
	}
	
	
	
	
	private RecentTransactionResponse toRecentTransactionResponse(TransactionListItemResponse item) {
		RecentTransactionResponse r = new RecentTransactionResponse();
		r.setDate(item.getDate());
		r.setDescription(item.getDescription());
		r.setAmount(item.getAmount());
		return r;
	}
}
