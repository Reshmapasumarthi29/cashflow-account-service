package com.belenits.cashflow.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.belenits.cashflow.account.entity.CashWalletDetails;

public interface CashWalletDetailsRepository extends JpaRepository<CashWalletDetails, Long>{

}
