package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;
import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountOverviewResponse {
	
	private BigDecimal totalBalance;
	
	private int totalAccounts;
	
	private List<AccountTypeSummaryResponse> accountsByType;
	
	private List<AccountSummaryResponse> accounts;

	
}
