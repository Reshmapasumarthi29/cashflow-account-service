package com.belenits.cashflow.account.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "investment_account_details")
public class InvestmentAccountDetails {

	@Id
    @Column(name = "account_id")
    private Long accountId;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(
                    name = "fk_investment_account_details_account"
            )
    )
    private Account account;


    @Column(
            name = "platform_name",
            length = 150
    )
    private String platformName;


    @Column(
            name = "invested_amount",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal investedAmount = BigDecimal.ZERO;


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
