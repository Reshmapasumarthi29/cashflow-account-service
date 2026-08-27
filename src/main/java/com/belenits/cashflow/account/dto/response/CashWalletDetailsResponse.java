package com.belenits.cashflow.account.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashWalletDetailsResponse implements AccountTypeDetails {
	
	private String providerName;
	
	private String walletIdentifier;
	

}
