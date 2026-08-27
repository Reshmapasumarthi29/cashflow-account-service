package com.belenits.cashflow.account.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "bank_account_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bank_user_institution_account_hash",
                        columnNames = {"user_id", "institution_id", "account_number_hash"}
                )
        }
)
public class BankAccountDetails {

    @Id
    @Column(name = "account_id")
    private Long accountId;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(
                    name = "fk_bank_account_details_account"
            )
    )
    private Account account;


    @Column(
            name = "account_number_encrypted",
            nullable = false,
            length = 500
    )
    private String accountNumberEncrypted;


    @Column(
            name = "account_number_last4",
            nullable = false,
            length = 4
    )
    private String accountNumberLast4;


    @Column(
            name = "account_number_hash",
            nullable = false,
            length = 64
    )
    private String accountNumberHash;


    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(name = "institution_id", nullable = false)
    private Long institutionId;


    @Column(
            name = "ifsc_code",
            length = 20
    )
    private String ifscCode;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at",
            nullable = false
    )
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
