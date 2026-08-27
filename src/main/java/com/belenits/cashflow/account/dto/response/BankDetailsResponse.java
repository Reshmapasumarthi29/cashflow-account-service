package com.belenits.cashflow.account.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankDetailsResponse implements AccountTypeDetails {
	
	private String accountNumberLast4;
	
	private String ifscCode;

}
