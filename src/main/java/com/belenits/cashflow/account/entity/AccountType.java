package com.belenits.cashflow.account.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="account_types")
@Getter
@Setter
public class AccountType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_type_id")
	private Long accountTypeId;
	
	@Column(name = "type_code", nullable = false, unique = true, length = 50)
	private String typeCode;
	
	@Column(name = "type_name", nullable = false, length = 100)
	private String typeName;
	
	@Column(length=255)
	private String description;	
	
	@Column(name = "is_active", nullable = false)
	private Boolean active = true;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	
    // One AccountType -> Many AccountSubtypes
    @OneToMany(mappedBy = "accountType")
    private List<AccountSubtype> subtypes = new ArrayList<>();


    // One AccountType -> Many InstitutionAccountType mappings
    @OneToMany(mappedBy = "accountType")
    private List<InstitutionAccountType> institutionAccountTypes =
            new ArrayList<>();
	
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
