package com.belenits.cashflow.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountTypeCountResponse {

	
	    private Long accountTypeId;
	    
	    private String typeCode;
	    
	    private String typeName;
	    
	    private String description;
	    
	    private Long accountCount;
}
