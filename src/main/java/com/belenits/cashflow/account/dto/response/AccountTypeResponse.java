package com.belenits.cashflow.account.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTypeResponse {
	
   private Long accountTypeId;
   
   private String typeCode;
   
   private String typeName;
   
   private String description;
   
   

}
