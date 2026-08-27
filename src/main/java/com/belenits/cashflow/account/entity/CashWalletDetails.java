package com.belenits.cashflow.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "cash_wallet_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cash_wallet_user_identity_hash",
                        columnNames = {"user_id", "wallet_identity_hash"}
                )
        }
)
public class CashWalletDetails {

    @Id
    @Column(name = "account_id")
    private Long accountId;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(
                    name = "fk_cash_wallet_details_account"
            )
    )
    private Account account;


    @Column(
            name = "provider_name",
            length = 150
    )
    private String providerName;


    @Column(
            name = "wallet_identifier",
            length = 255
    )
    private String walletIdentifier;


    @Column(name = "wallet_identity_hash", length = 64)
    private String walletIdentityHash;


    @Column(name = "user_id", nullable = false)
    private Long userId;


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