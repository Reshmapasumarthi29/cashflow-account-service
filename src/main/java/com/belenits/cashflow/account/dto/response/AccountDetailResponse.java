package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.belenits.cashflow.account.entity.AccountStatus;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccountDetailResponse {

	private Long accountId;
	
	private String accountName;
	
	private String accountTypeName;
	
	private String typeCode;
	
	private String institutionName;
	
	private String currencyCode;
	
	private BigDecimal currentBalance;
	
	private BigDecimal availableBalance;
	
	private AccountStatus status;
	
	private AccountTypeDetails typeSpecificDetails;
	
	private List<RecentTransactionResponse> recentTransactions;
	
	private boolean transactionsLoadFailed;
	
	
	
	
	
}
