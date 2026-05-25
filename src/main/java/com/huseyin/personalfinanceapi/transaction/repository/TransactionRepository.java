package com.huseyin.personalfinanceapi.transaction.repository;

import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserIdAndType(Long userId, TransactionType type);

    List<Transaction> findByTimeBetween(Instant start, Instant end);

    List<Transaction> findByDescriptionContainingIgnoreCase(String keyword);

    @Query("""
            SELECT DISTINCT t FROM Transaction t
              JOIN FETCH t.entryList e
             WHERE e.account.id = :accountId
            """)
    List<Transaction> findAllByAccountId(@Param("accountId") Long accountId);

    @Query("""
            SELECT DISTINCT t FROM Transaction t
              JOIN FETCH t.entryList e
             WHERE e.account.id = :accountId
               AND t.user.id = :userId
            """)
    List<Transaction> findAllByAccountIdAndUserId(@Param("accountId") Long accountId,
                                                  @Param("userId") Long userId);

    @Query("""
            SELECT DISTINCT t FROM Transaction t
              JOIN FETCH t.entryList e
             WHERE e.account.id = :accountId AND e.direction = :direction
            """)
    List<Transaction> findByAccountIdAndDirection(@Param("accountId") Long accountId,
                                                  @Param("direction") Entry.Direction direction);

    /**
     * Bu hesaba INITIAL_BALANCE dışında herhangi bir entry düşmüş mü? Talimattaki
     * "hesaba başka bir işlem girildikten sonra bir daha başlangıç bakiyesi
     * eklenemez" kuralını uygulamak için.
     */
    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
              FROM Entry e
             WHERE e.account.id = :accountId
               AND e.transaction.type <> :type
               AND e.transaction.reverse = false
            """)
    boolean existsNonReversedNonInitialBalanceForAccount(@Param("accountId") Long accountId,@Param("type") TransactionType type);

    /** Bu hesap için zaten bir INITIAL_BALANCE oluşturulmuş mu? */
    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
              FROM Entry e
             WHERE e.account.id = :accountId
               AND e.transaction.type = :type
            """)
    boolean existsInitialBalanceForAccount(@Param("accountId") Long accountId,@Param("type") TransactionType type);

    @Query("""
    SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
    FROM Entry e
    WHERE e.account.id = :accountId
      AND e.transaction.type = :type
      AND e.transaction.reversed = false
    """)
    boolean existsNonReversedInitialBalanceForAccount(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type
    );
}
