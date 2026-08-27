package com.belenits.cashflow.account.mapper;

import org.springframework.stereotype.Component;

import com.belenits.cashflow.account.dto.response.AccountTypeResponse;
import com.belenits.cashflow.account.entity.AccountType;

@Component
public class AccountTypeMapper {
	
	public AccountTypeResponse toAccountTypeResponse(AccountType accountType) {
		
		AccountTypeResponse response = new AccountTypeResponse();
		response.setAccountTypeId(accountType.getAccountTypeId());
		response.setTypeCode(accountType.getTypeCode());
		response.setTypeName(accountType.getTypeName());
		response.setDescription(accountType.getDescription());
		
		return response;
	}
	

}
