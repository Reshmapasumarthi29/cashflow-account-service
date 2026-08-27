package com.belenits.cashflow.account.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
	    name = "financial_institutions",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_institution_code",
	            columnNames = "institution_code"
	        ),
	        @UniqueConstraint(
	            name = "uk_institution_name",
	            columnNames = "institution_name"
	        )
	    }
	)
public class FinancialInstitution {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="institution_id")
	private Long institutionId;;
	
	@Column(name="institution_code", nullable=false, length=50)
	private String institutionCode;
	
	@Column(name="institution_name", nullable=false, length=150)
	private String institutionName;
	
	@Column(name="is_active", nullable=false)
	private Boolean active = true;    
	
    /*
     * One Financial Institution can support
     * multiple Account Types.
     *
     * Example:
     *
     * HDFC Bank
     *   ├── BANK
     *   ├── CREDIT_CARD
     *   └── LOAN
     */
    @OneToMany(
        mappedBy = "financialInstitution",
        fetch = FetchType.LAZY
    )
    private List<InstitutionAccountType> institutionAccountTypes =
            new ArrayList<>();
    
    /*
     * One Financial Institution can have
     * many user Accounts.
     *
     * Example:
     *
     * HDFC Bank
     *   ├──  Savings Account
     *   ├──  Credit Card
     *   └── Another User's Current Account
     */
    @OneToMany(
        mappedBy = "financialInstitution",
        fetch = FetchType.LAZY
    )
    private List<Account> accounts = new ArrayList<>();
	
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
