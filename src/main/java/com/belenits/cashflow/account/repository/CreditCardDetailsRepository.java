package com.belenits.cashflow.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.belenits.cashflow.account.entity.CreditCardDetails;

public interface CreditCardDetailsRepository extends JpaRepository<CreditCardDetails, Long>{

}
