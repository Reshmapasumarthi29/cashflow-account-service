package com.belenits.cashflow.account.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "accounts",
        indexes = {

                @Index(
                        name = "idx_accounts_user_id",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_accounts_user_status",
                        columnList = "user_id, status"
                ),

                @Index(
                        name = "idx_accounts_type",
                        columnList = "account_type_id"
                )
        }
)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

 

    @Column(name = "user_id", nullable = false)
    private Long userId;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn( name = "account_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_type")
    )
    private AccountType accountType;


    @Column(name = "account_subtype_id")
    private Long accountSubtypeId;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(
                    name = "account_subtype_id",
                    referencedColumnName = "account_subtype_id",
                    insertable = false,
                    updatable = false
            ),
            @JoinColumn(
                    name = "account_type_id",
                    referencedColumnName = "account_type_id",
                    insertable = false,
                    updatable = false
            )
    })
    private AccountSubtype accountSubtype;
    
    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "account_category_id",
//            foreignKey = @ForeignKey(name = "fk_account_category")
//    )
//    private AccountCategory accountCategory;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "institution_id",
            foreignKey = @ForeignKey(name = "fk_account_institution")
    )
    private FinancialInstitution financialInstitution;


    @Column( name = "account_name",nullable = false,length = 150 )
    private String accountName;


    @Column(   name = "currency_code", nullable = false, length = 3 )
    private String currencyCode = "INR";
    
    @Column( name = "opening_balance", nullable = false,precision = 19,  scale = 4 )
    private BigDecimal openingBalance;
    
    @Column( name = "current_balance",  nullable = false, precision = 19, scale = 4)
    private BigDecimal currentBalance;


    @Column( name = "available_balance", precision = 19, scale = 4 )
    private BigDecimal availableBalance;   
    
    
    //Enum change required
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20 )
    private AccountStatus status = AccountStatus.ACTIVE;
    
    @Column( name = "created_at",nullable = false, updatable = false )
    private LocalDateTime createdAt;
    
    @Column( name = "updated_at",nullable = false)
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
    
    @OneToOne( mappedBy = "account",fetch = FetchType.LAZY )
    private BankAccountDetails bankAccountDetails; 

    @OneToOne(  mappedBy = "account", fetch = FetchType.LAZY )
    private CreditCardDetails creditCardDetails;


    @OneToOne(  mappedBy = "account", fetch = FetchType.LAZY )
    private CashWalletDetails cashWalletDetails;


    @OneToOne(  mappedBy = "account", fetch = FetchType.LAZY )
    private InvestmentAccountDetails investmentAccountDetails;


    @OneToOne( mappedBy = "account", fetch = FetchType.LAZY  )
    private LoanAccountDetails loanAccountDetails;
 
	
}

