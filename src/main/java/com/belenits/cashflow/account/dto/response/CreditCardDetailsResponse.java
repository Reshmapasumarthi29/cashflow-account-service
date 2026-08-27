package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCardDetailsResponse implements AccountTypeDetails {
	
	private String cardNumberLast4;
	
	private BigDecimal creditLimit;
	
	private Integer billingCycleDay;
	
	private Integer paymentDueDay;

}
