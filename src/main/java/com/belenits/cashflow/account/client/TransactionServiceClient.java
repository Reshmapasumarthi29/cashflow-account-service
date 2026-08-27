package com.belenits.cashflow.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.belenits.cashflow.account.dto.response.TransactionListResponse;

@FeignClient(name = "transaction-service", url ="${transaction.service.url}")
public interface TransactionServiceClient {
	
	@GetMapping("/api/v1/transactions")
	TransactionListResponse getTransactions(
			                    @RequestParam("userId") Long userId,
			                    @RequestParam("accountId") Long accountId,
			                    @RequestParam("page") int page,
			                    @RequestParam("size") int size
			                         );
	

}
