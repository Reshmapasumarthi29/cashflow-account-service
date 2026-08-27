package com.belenits.cashflow.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseResponse<T> {
	
	private int statusCode;
	
	private String message;
	
	private Boolean success;
	
	private T data;
	
	private String correlationId;

}
