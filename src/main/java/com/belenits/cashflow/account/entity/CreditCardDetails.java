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
        name = "credit_card_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_credit_card_user_card_hash",
                        columnNames = {"user_id", "card_number_hash"}
                )
        }
)
public class CreditCardDetails {

    @Id
    @Column(name = "account_id")
    private Long accountId;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(
                    name = "fk_credit_card_details_account"
            )
    )
    private Account account;


    @Column(
            name = "card_number_encrypted",
            nullable = false,
            length = 500
    )
    private String cardNumberEncrypted;


    @Column(
            name = "card_number_last4",
            nullable = false,
            length = 4
    )
    private String cardNumberLast4;


    @Column(
            name = "card_number_hash",
            nullable = false,
            length = 64
    )
    private String cardNumberHash;


    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(
            name = "credit_limit",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal creditLimit;


    @Column(name = "billing_cycle_day")
    private Integer billingCycleDay;


    @Column(name = "payment_due_day")
    private Integer paymentDueDay;


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