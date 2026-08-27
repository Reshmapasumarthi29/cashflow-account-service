package com.belenits.cashflow.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
	
	private Boolean success;
	
	private int statusCode;
	
	private String errorMessage;
	
	private String correlationId;

}
