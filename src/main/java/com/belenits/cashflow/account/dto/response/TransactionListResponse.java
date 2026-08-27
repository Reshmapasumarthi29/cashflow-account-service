package com.belenits.cashflow.account.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionListResponse {

	private boolean success;
	
	private TransactionListPageResponse data;
	
	private String correlationId;
}
