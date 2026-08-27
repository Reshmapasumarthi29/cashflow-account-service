package com.belenits.cashflow.account.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionListPageResponse {

	private List<TransactionListItemResponse> content;
	
	private int page;
	
	private int size;
	
	private long totalElements;
	
	private int totalPages;
}
