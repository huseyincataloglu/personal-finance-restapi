package com.huseyin.personalfinanceapi.transaction.entity;

import com.huseyin.personalfinanceapi.category.entity.Category;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "transactions",
        indexes = {
                @Index(name = "idx_tx_user", columnList = "user_id"),
                @Index(name = "idx_tx_type", columnList = "type"),
                @Index(name = "idx_tx_time", columnList = "time")
        })
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String description;

    @Column(nullable = false,updatable = false)
    private Instant time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32,updatable = false)
    private TransactionType type;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Entry> entryList = new ArrayList<>();

    // eğer bir exchange tipine işlem ise bunu
    @Column(precision = 19, scale = 8)
    private BigDecimal appliedRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * For Reversal and Refund transactions
     */
    @Column(name = "referencetx_id")
    private Long referenceTransactionId;

    /** Bu orijinal işlemin terslendiğini gösteren flag (REVERSE oluşturulunca true). */
    @Column(nullable = false)
    private boolean reversed = false;

    //For transactions that can be refunded, null for other non-refunded ones
    @Column(name = "is_refunded")
    private Boolean isRefund;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public void addEntry(Entry e) {
        e.setTransaction(this); // ! Transaction are bind with entries inside the transaction object's itself
        entryList.add(e);
    }

    public Transaction reverse(){
        this.reversed = true;

        Transaction reverseTx = new Transaction();
        reverseTx.setUser(this.user);
        reverseTx.setType(TransactionType.REVERSAL);
        reverseTx.setDescription(this.description + "ERROR CORRECTION");
        reverseTx.setReversed(false);
        reverseTx.setTime(Instant.now());
        reverseTx.setReferenceTransactionId(this.id);
        return reverseTx;

    }


}
