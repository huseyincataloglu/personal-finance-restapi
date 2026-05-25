package com.huseyin.personalfinanceapi.transaction.entry;

import com.huseyin.personalfinanceapi.common.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cash_entries")
@DiscriminatorValue("CASH")
@Getter
@Setter
@NoArgsConstructor
public class CashEntry extends Entry {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "cash_amount", precision = 19, scale = 4, nullable = false)),
            @AttributeOverride(name = "currencyCode",
                    column = @Column(name = "cash_currency", length = 3, nullable = false))
    })
    private Money amount;

    public CashEntry(Money amount, Direction direction) {
        this.amount = amount;
        setDirection(direction);
        setEntryType(EntryType.CASH);
    }

    @Override
    public Entry reverse() {
        CashEntry reversedCashEntry = new CashEntry();
        reversedCashEntry.setEntryType(EntryType.CASH);
        reversedCashEntry.setAccount(this.getAccount());
        reversedCashEntry.setDirection(this.getDirection().reverse());
        reversedCashEntry.setAmount(this.getAmount());
        return reversedCashEntry;
    }
}
