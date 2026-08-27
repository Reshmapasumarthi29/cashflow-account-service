package com.belenits.cashflow.account.controller;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.belenits.cashflow.account.dto.response.AccountDetailResponse;
import com.belenits.cashflow.account.dto.response.AccountOverviewResponse;
import com.belenits.cashflow.account.dto.response.BaseResponse;
import com.belenits.cashflow.account.security.JwtService;
import com.belenits.cashflow.account.security.Permissions;
import com.belenits.cashflow.account.service.AccountDetailService;
import com.belenits.cashflow.account.service.AccountOverviewService;
import com.belenits.cashflow.account.util.CorrelationIdUtil;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
public class AccountReadController {
	
	private final AccountOverviewService accountOverviewService;
	
	private final AccountDetailService accountDetailService;
	
	private final JwtService jwtService;
	
	
	@GetMapping("/overview")
	@PreAuthorize("hasAuthority('" + Permissions.ACCOUNT_VIEW + "')")
	public ResponseEntity<BaseResponse<AccountOverviewResponse>> getAccountOverview(@RequestHeader("Authorization") String authHeader){
		
		
		Long userId = jwtService.extractUserId(authHeader);
		
		log.info("GET /api/v1/accounts/{}/overview - request received", userId);

		String correlationId = MDC.get(CorrelationIdUtil.MDC_KEY);
		
		AccountOverviewResponse overviewResponse = accountOverviewService.getAccountOverview(userId);
		log.info("Account overview fetched successfully for userId={}", userId);

		
	    BaseResponse<AccountOverviewResponse> response = new BaseResponse<>(
	     				                                              200,
	     				                                              "Account overview retrieved successfully",
	     				                                              true,
	     				                                              overviewResponse,
	     				                                              correlationId);
	    log.debug("Overview response prepared for userId={}", userId);
	    return ResponseEntity.status(HttpStatus.OK).body(response);
		
	}
	
	
    @GetMapping("/{accountId}")
    @PreAuthorize("hasAuthority('" + Permissions.ACCOUNT_VIEW + "')")
    public ResponseEntity<BaseResponse<AccountDetailResponse>> getAccountDetail(@PathVariable("accountId")@Positive Long accountId,
    		                                                                   @RequestHeader("Authorization") String authHeader) {

    	Long userId = jwtService.extractUserId(authHeader);
    	
    	log.info("GET /api/v1/accounts/{}?userId={} - request received", accountId, userId);
    	
    	String correlationId = MDC.get(CorrelationIdUtil.MDC_KEY);
    	
        AccountDetailResponse detailResponse = accountDetailService.getAccountDetailById(accountId, userId);
        
        log.info("Account detail fetched successfully for accountId={}, userId={}", accountId, userId);

        BaseResponse<AccountDetailResponse> response = new BaseResponse<>(
                200, "Account details retrieved successfully", true, detailResponse, correlationId);

        log.debug("Detail response prepared for accountId={}, userId={}", accountId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
