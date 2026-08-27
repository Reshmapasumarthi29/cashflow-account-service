package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestmentDetailsResponse implements AccountTypeDetails {
	
	private String platformName;
	
	private BigDecimal investedAmount;

}
