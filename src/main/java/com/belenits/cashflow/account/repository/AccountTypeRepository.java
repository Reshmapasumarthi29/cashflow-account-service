package com.belenits.cashflow.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.belenits.cashflow.account.dto.response.AccountTypeCountResponse;
import com.belenits.cashflow.account.entity.AccountType;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
	
	@Query("SELECT a FROM AccountType a WHERE a.active = true")
	 List<AccountType> findByActive();
	
	@Query("""
		    SELECT new com.belenits.cashflow.account.dto.response.AccountTypeCountResponse(
		        at.accountTypeId, at.typeCode, at.typeName, at.description, COUNT(a.accountId))
		    FROM AccountType at
		    LEFT JOIN Account a ON a.accountType = at AND a.userId = :userId
		    GROUP BY at.accountTypeId, at.typeCode, at.typeName, at.description
		    """)
		List<AccountTypeCountResponse> getAccountTypeCounts(@Param("userId") Long userId);

	 
}
