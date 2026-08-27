package com.belenits.cashflow.account.controller;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.belenits.cashflow.account.dto.response.AccountTypeCountResponse;
import com.belenits.cashflow.account.dto.response.BaseResponse;
import com.belenits.cashflow.account.security.JwtUtil;
import com.belenits.cashflow.account.service.AccountTypeCountService;
import com.belenits.cashflow.account.util.CorrelationIdUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account-types")
public class AccountTypeController {

	private final AccountTypeCountService accountTypeService;
	
	private final JwtUtil jwtUtil;
	
	
	
	
	@GetMapping("/counts")
	public ResponseEntity<BaseResponse<List<AccountTypeCountResponse>>> getAccountTypeCount(@RequestHeader("Authorization") String authHeader){
	
		Long userId = jwtUtil.extractUserId(authHeader);
		
		
		log.info("GET /api/v1/account-types/{}/counts - request received", userId);
		
		String correlationId = MDC.get(CorrelationIdUtil.MDC_KEY);
		
		List<AccountTypeCountResponse> counts = accountTypeService.getAccountTypeCounts(userId);
		
		log.info("Account type counts fetched successfully for userId={}", userId);
		
		BaseResponse<List<AccountTypeCountResponse>> response = new BaseResponse<>(200,
				                                                              "Account type counts retrieved successfully",
				                                                              true,
				                                                              counts,
				                                                              correlationId);	
		
		 log.debug("Account type count response prepared for userId={}, countSize={}", userId, counts.size());
		return ResponseEntity.status(HttpStatus.OK).body(response);
				
	}
	
	
}
