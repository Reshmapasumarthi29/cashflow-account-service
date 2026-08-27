package com.belenits.cashflow.account.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="institution_account_types",
           	uniqueConstraints = {
           			@UniqueConstraint(name="uk_institution_account_type",
           					columnNames = {"institution_id","account_type_id"})
           	}
		)

public class InstitutionAccountType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="institution_account_type_id")
	private Long institutionAccountTypeid;
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "institution_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_iat_institution")
    )
    private FinancialInstitution financialInstitution;
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "account_type_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_iat_account_type")
    )
    private AccountType accountType;
	
	@Column(name="is_active", nullable=false)
	private Boolean isActive = true;
	
	@Column(name="created_at", nullable=false, updatable=false)
	private LocalDateTime createdAt;
	
	@Column(name="updated_at", nullable=false)
	private LocalDateTime updatedAt;
	
	
	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
