package com.belenits.cashflow.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.belenits.cashflow.account.entity.InstitutionAccountType;

public interface InstitutionAccountTypeRepository extends JpaRepository<InstitutionAccountType, Long>{

}
