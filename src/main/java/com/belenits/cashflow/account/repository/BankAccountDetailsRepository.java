package com.belenits.cashflow.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.belenits.cashflow.account.entity.BankAccountDetails;

public interface BankAccountDetailsRepository extends JpaRepository<BankAccountDetails, Long>{

}
