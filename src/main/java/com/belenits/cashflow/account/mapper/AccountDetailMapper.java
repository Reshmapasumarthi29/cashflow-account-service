package com.belenits.cashflow.account.mapper;

import org.springframework.stereotype.Component;

import com.belenits.cashflow.account.dto.response.AccountDetailResponse;
import com.belenits.cashflow.account.entity.Account;

@Component
public class AccountDetailMapper {


	    public AccountDetailResponse toAccountDetailResponse(Account account) {

	        AccountDetailResponse response = new AccountDetailResponse();
	        response.setAccountId(account.getAccountId());
	        response.setAccountName(account.getAccountName());
	        response.setTypeCode(account.getAccountType().getTypeCode());
	        response.setAccountTypeName(account.getAccountType().getTypeName());
	        response.setInstitutionName(
	                account.getFinancialInstitution() != null
	                        ? account.getFinancialInstitution().getInstitutionName()
	                        : null);
	        response.setCurrencyCode(account.getCurrencyCode());
	        response.setCurrentBalance(account.getCurrentBalance());
	        response.setAvailableBalance(account.getAvailableBalance());
	        response.setStatus(account.getStatus());

	        return response;
	    }
	}
