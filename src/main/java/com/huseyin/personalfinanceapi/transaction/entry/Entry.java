package com.huseyin.personalfinanceapi.transaction.entry;


import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Bir Transaction'ın tek bir hesap üzerindeki etkisini ifade eder. Talimat
 * gereği iki alt türü vardır: {@link CashEntry} (nakit hareket) ve
 * {@link AssetEntry} (varlık hareketi).
 *
 * <p>Direction:
 * <ul>
 *   <li>{@code INWARD} — hesaba giriş (income, transferin hedefi, alımın hedefi)</li>
 *   <li>{@code OUTWARD} — hesaptan çıkış (expense, transferin kaynağı, satışın kaynağı)</li>
 * </ul>
 */
@Entity
@Table(name = "entries",
        indexes = {
                @Index(name = "idx_entry_account", columnList = "account_id"),
                @Index(name = "idx_entry_transaction", columnList = "transaction_id")
        })
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "entry_kind", discriminatorType = DiscriminatorType.STRING, length = 16)
@Getter
@Setter
@NoArgsConstructor
public abstract class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8,updatable = false)
    private EntryType entryType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8,updatable = false)
    private Direction direction;



    public enum EntryType {
        CASH,
        ASSET
    }

    public enum Direction {
        INWARD,
        OUTWARD;

        public Direction reverse() {
            return this == INWARD ? OUTWARD : INWARD;
        }

        public static Direction from(String direction) {
            Optional<Direction> filteredDirection = Arrays.stream(Direction.values())
                    .filter(it -> it.name().equalsIgnoreCase(direction)).findFirst();
            return filteredDirection.orElseThrow(
                    () -> new NotAValidDirection("Directions are " + Arrays.toString(Direction.values())));
        }
    }
    public abstract Entry reverse();


}
