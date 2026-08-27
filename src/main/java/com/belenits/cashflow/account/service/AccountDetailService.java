package com.belenits.cashflow.account.service;

import com.belenits.cashflow.account.dto.response.AccountDetailResponse;

public interface AccountDetailService {
	
	AccountDetailResponse getAccountDetailById(Long accountId, Long userId);

}
