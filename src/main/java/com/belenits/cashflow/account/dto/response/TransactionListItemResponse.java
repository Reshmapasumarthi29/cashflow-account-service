package com.belenits.cashflow.account.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionListItemResponse {

	private Long transactionId;
	
	private LocalDate date;
	
	private String description;
	
	private String type;
	
	private BigDecimal amount;
	
	private String currency;
	
	private String category;
	
	private String merchant;
	
	private String accountName;
	
	private String status;
}
