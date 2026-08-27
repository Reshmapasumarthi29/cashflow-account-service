package com.belenits.cashflow.account.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.belenits.cashflow.account.dto.response.AccountSummaryResponse;
import com.belenits.cashflow.account.dto.response.AccountTypeSummaryResponse;
import com.belenits.cashflow.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

	@Query("SELECT SUM(a.currentBalance) FROM Account a WHERE a.userId =:userId AND a.status ='ACTIVE' ")
	BigDecimal getTotalBalance(@Param("userId") Long userId);

	@Query("SELECT COUNT(a) FROM Account a WHERE a.userId =:userId AND a.status = 'ACTIVE' ")
	int getTotalAccounts(@Param("userId") Long userId);

	@Query("""
			SELECT new com.belenits.cashflow.account.dto.response.AccountTypeSummaryResponse(
			                                                            a.accountType.accountTypeId,
			                                                             a.accountType.typeName,
			                                                             SUM(a.currentBalance),
			                                                             null
			                                                             )
			                                                            FROM Account a
			                                                            WHERE a.userId = :userId AND a.status = 'ACTIVE'
			                                                            GROUP BY a.accountType.accountTypeId, a.accountType.typeName
			                                                           """)
	List<AccountTypeSummaryResponse> getAccountTypeSummaries(@Param("userId") Long userId);

	@Query("""
			SELECT new com.belenits.cashflow.account.dto.response.AccountSummaryResponse(
			    a.accountId,
			    a.accountName,
			    a.accountType.typeName,
			    CASE
			       WHEN a.accountType.typeCode IN ('CREDIT_CARD', 'LOAN') THEN a.currentBalance * -1
			       ELSE a.currentBalance
			    END,
			    CASE
			        WHEN a.accountType.typeCode = 'BANK' THEN b.accountNumberLast4
			        WHEN a.accountType.typeCode = 'CREDIT_CARD' THEN c.cardNumberLast4
			        WHEN a.accountType.typeCode = 'LOAN' THEN l.loanAccountNumberLast4
			        ELSE NULL
			    END,
			    a.status
			)
			FROM Account a
			LEFT JOIN a.bankAccountDetails b
			LEFT JOIN a.creditCardDetails c
			LEFT JOIN a.loanAccountDetails l
			WHERE a.userId = :userId
			ORDER BY
			        CASE a.status
			      WHEN com.belenits.cashflow.account.entity.AccountStatus.ACTIVE THEN 1
			      WHEN com.belenits.cashflow.account.entity.AccountStatus.INACTIVE THEN 2
			      WHEN com.belenits.cashflow.account.entity.AccountStatus.CLOSED THEN 3
			  END
			""")
	List<AccountSummaryResponse> getAccountSummaries(@Param("userId") Long userId);

}
