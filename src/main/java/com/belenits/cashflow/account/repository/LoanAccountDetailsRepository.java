package com.belenits.cashflow.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.belenits.cashflow.account.entity.LoanAccountDetails;

public interface LoanAccountDetailsRepository extends JpaRepository<LoanAccountDetails, Long>{

}
