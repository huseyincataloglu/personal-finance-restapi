package com.huseyin.personalfinanceapi.account.entity;

import com.huseyin.personalfinanceapi.account.AccountStatus;
import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Tüm hesap türlerinin abstract üst sınıfı.
 *
 * <p>JOINED inheritance kullanıyoruz; alt sınıflar (BalanceAccount / AssetAccount)
 * kendi tablolarına ek alanlarını yazar. {@code account_category} discriminator
 * kolonu BALANCE / ASSET ayrımını veritabanı seviyesinde tutar; {@link AccountType}
 * ise WALLET, BANK, SAVINGS, CREDIT_CARD vb. gibi ürün/iş ayrımıdır.
 */
@Entity
@Table(name = "accounts",
        uniqueConstraints = @UniqueConstraint(
                name = "account_name_type",
                columnNames = {"user_id", "name", "type"}),
        indexes = {
                @Index(name = "idx_account_user", columnList = "user_id"),
                @Index(name = "idx_account_user_type", columnList = "user_id,type")
        })
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_category", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 32)
    private AccountStatus status;

    private boolean isDefault; // gets false value because instance varaibles are assigned by their default values if they are not assigned explicitly

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
