package com.belenits.cashflow.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "loan_account_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_loan_user_loan_hash",
                        columnNames = {"user_id", "loan_account_number_hash"}
                )
        }
)
public class LoanAccountDetails {

    @Id
    @Column(name = "account_id")
    private Long accountId;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(
                    name = "fk_loan_account_details_account"
            )
    )
    private Account account;


    @Column(
            name = "loan_account_number_encrypted",
            length = 500
    )
    private String loanAccountNumberEncrypted;


    @Column(
            name = "loan_account_number_last4",
            length = 4
    )
    private String loanAccountNumberLast4;


    @Column(name = "loan_account_number_hash", length = 64)
    private String loanAccountNumberHash;


    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(
            name = "original_loan_amount",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal originalLoanAmount;


    @Column(
            name = "interest_rate",
            precision = 7,
            scale = 4
    )
    private BigDecimal interestRate;


    @Column(
            name = "emi_amount",
            precision = 19,
            scale = 4
    )
    private BigDecimal emiAmount;


    @Column(name = "emi_due_day")
    private Integer emiDueDay;


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

    // Getters and Setters
}