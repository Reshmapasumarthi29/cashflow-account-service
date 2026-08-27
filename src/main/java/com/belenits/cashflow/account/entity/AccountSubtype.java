package com.belenits.cashflow.account.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "account_subtypes",
        uniqueConstraints = {

                @UniqueConstraint(
                        name = "uk_type_subtype_code",
                        columnNames = {
                                "account_type_id",
                                "subtype_code"
                        }
                ),

                @UniqueConstraint(
                        name = "uk_subtype_type",
                        columnNames = {
                                "account_subtype_id",
                                "account_type_id"
                        }
                )
        }
)
public class AccountSubtype {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_subtype_id") 
	private Long accountSubtypeId;
	
	
	@Column(name="subtype_code" , nullable = false, length = 50)
	private String subtypeCode;
	
	@Column(name= "subtype_name", nullable = false, length = 100)
	private String subtypeName;
	
	@Column(name= "description", length = 255)
	private String description;
	
	@Column(name= "is_active", nullable = false)
	private Boolean active = true;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name ="updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "account_type_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_subtype_account_type"
            )
    )
    private AccountType accountType;


    // Read-only mirror used by composite foreign-key references from Account.
    @Column(name = "account_type_id", insertable = false, updatable = false)
    private Long accountTypeId;
	
	
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


