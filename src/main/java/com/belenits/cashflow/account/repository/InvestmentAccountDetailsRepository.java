package com.belenits.cashflow.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.belenits.cashflow.account.entity.InvestmentAccountDetails;

public interface InvestmentAccountDetailsRepository extends JpaRepository<InvestmentAccountDetails, Long>{

}
