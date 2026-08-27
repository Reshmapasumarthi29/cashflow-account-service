package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecentTransactionResponse {

	private LocalDate date;
	
	private String description;
	
	private BigDecimal amount; 
}
