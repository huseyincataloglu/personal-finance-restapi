package com.huseyin.personalfinanceapi.account.entity;

import com.huseyin.personalfinanceapi.common.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Skaler bakiyesi {@link Money} olarak tutulan hesap (Cüzdan, Banka, Birikim,
 * Alacak, Borç, Kredi Kartı, Kredi). Bakiye, Money sınıfının immutability
 * davranışına saygı duyularak {@code applyDelta} metotlarıyla yeni bir Money
 * üretilerek değiştirilir.
 */
@Entity
@Table(name = "balance_accounts")
@DiscriminatorValue("BALANCE")
@Getter
@Setter
@NoArgsConstructor
public class BalanceAccount extends Account {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "balance_amount", precision = 19, scale = 4, nullable = false)),
            @AttributeOverride(name = "currencyCode",
                    column = @Column(name = "balance_currency", length = 3, nullable = false))
    })
    private Money balance;
}
