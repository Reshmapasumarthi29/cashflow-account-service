package com.belenits.cashflow.account.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.belenits.cashflow.account.dto.response.AccountOverviewResponse;
import com.belenits.cashflow.account.dto.response.AccountSummaryResponse;
import com.belenits.cashflow.account.dto.response.AccountTypeSummaryResponse;
import com.belenits.cashflow.account.repository.AccountRepository;
import com.belenits.cashflow.account.service.AccountOverviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOverviewServiceImpl implements AccountOverviewService {

	private final AccountRepository accountRepository;

	@Override
	public AccountOverviewResponse getAccountOverview(Long userId) {

		log.info("Fetching account overview for userId={}", userId);

		BigDecimal totalBalance = accountRepository.getTotalBalance(userId);
		if (totalBalance == null) {
			log.debug("Total balance is null for userId={}, defaulting to 0", userId);
			totalBalance = BigDecimal.ZERO;
		}

		int totalAccounts = accountRepository.getTotalAccounts(userId);
		log.debug("Total accounts for userId={} is {}", userId, totalAccounts);

		List<AccountTypeSummaryResponse> accountTypeSummaries = accountRepository.getAccountTypeSummaries(userId);
		log.debug("Fetched {} account-type summaries for userId={}",
				accountTypeSummaries != null ? accountTypeSummaries.size() : 0, userId);

		for (AccountTypeSummaryResponse accountTypeSummary : accountTypeSummaries) {

			BigDecimal percentage = BigDecimal.ZERO;
			if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
				percentage = accountTypeSummary.getTotalBalance().multiply(BigDecimal.valueOf(100)).divide(totalBalance,
						0, RoundingMode.HALF_UP);

			}

			accountTypeSummary.setPercentage(percentage);
		}

		accountTypeSummaries.sort((a, b) -> b.getTotalBalance().compareTo(a.getTotalBalance()));

		log.debug("Sorted account-type summaries by totalBalance for userId={}", userId);

		List<AccountSummaryResponse> accountSummaries = accountRepository.getAccountSummaries(userId);
		log.debug("Fetched {} account summaries for userId={}", accountSummaries != null ? accountSummaries.size() : 0,
				userId);

		AccountOverviewResponse response = new AccountOverviewResponse();
		response.setTotalBalance(totalBalance);
		response.setTotalAccounts(totalAccounts);
		response.setAccountsByType(accountTypeSummaries);
		response.setAccounts(accountSummaries);

		log.info("Account overview prepared for userId={} with totalAccounts={} and totalBalance={}", userId,
				totalAccounts, totalBalance);

		return response;
	}

}
