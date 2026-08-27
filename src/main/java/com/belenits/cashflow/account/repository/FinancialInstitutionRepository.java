package com.belenits.cashflow.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.belenits.cashflow.account.entity.FinancialInstitution;

public interface FinancialInstitutionRepository extends JpaRepository<FinancialInstitution, Long>{

}
