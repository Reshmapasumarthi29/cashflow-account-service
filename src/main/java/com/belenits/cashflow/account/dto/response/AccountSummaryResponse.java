package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;

import com.belenits.cashflow.account.entity.AccountStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummaryResponse {
	
	private Long accountId;
	
	private String accountName;
	
	private String accountTypeName;
	
	private BigDecimal balance;
	
	private String maskedIdentifier;
	
	private AccountStatus status;

}
