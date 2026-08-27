package com.belenits.cashflow.account.service;

import com.belenits.cashflow.account.dto.response.AccountOverviewResponse;

public interface AccountOverviewService {
	
	AccountOverviewResponse getAccountOverview(Long userId);

}
