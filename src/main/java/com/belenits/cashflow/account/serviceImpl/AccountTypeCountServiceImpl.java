package com.belenits.cashflow.account.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;


import com.belenits.cashflow.account.dto.response.AccountTypeCountResponse;
import com.belenits.cashflow.account.repository.AccountTypeRepository;
import com.belenits.cashflow.account.service.AccountTypeCountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountTypeCountServiceImpl implements AccountTypeCountService{
	
	private final AccountTypeRepository accountTypeRepository;
	


	@Override
	public List<AccountTypeCountResponse> getAccountTypeCounts(Long userId) {
		
	    log.info("Fetching account type counts for userId={}", userId);

	    
		List<AccountTypeCountResponse> responses = accountTypeRepository.getAccountTypeCounts(userId);
		
		 log.info("Fetched {} account type count records for userId={}",
	                responses != null ? responses.size() : 0, userId);
		
		return responses;
	}


	
	
}
