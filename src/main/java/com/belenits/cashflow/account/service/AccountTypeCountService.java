package com.belenits.cashflow.account.service;

import java.util.List;



import com.belenits.cashflow.account.dto.response.AccountTypeCountResponse;


public interface AccountTypeCountService {
	
	 List<AccountTypeCountResponse> getAccountTypeCounts(Long userId);
	
	

}
