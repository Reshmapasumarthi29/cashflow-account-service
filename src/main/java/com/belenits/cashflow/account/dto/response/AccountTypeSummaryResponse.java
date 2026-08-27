package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTypeSummaryResponse {
	
	private Long accountTypeId;
	
	private String accountTypeName;
	
	private BigDecimal totalBalance;
	
	private BigDecimal percentage;

}
