package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanDetailsResponse implements AccountTypeDetails {
	
	private String loanAccountNumberLast4;
	
	private BigDecimal loanAmount;
	
	private BigDecimal interestRate;
	
	private BigDecimal emiAmount;
	
	private Integer emiDueDay;

}
