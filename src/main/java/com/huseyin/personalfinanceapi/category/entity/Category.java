package com.huseyin.personalfinanceapi.category.entity;


import com.huseyin.personalfinanceapi.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "categories",

        uniqueConstraints = @UniqueConstraint(columnNames = {"name","type","user_id"}),
        indexes = {
                @Index(name = "idx_category_user", columnList = "user_id"),
                @Index(name = "idx_category_type", columnList = "type")
        })
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    // True for built-in system categories, False for user created categories.
    @Column(nullable = false)
    private boolean builtIn;


    /**
     * @ManyToOne → Many = Category, One = User
     * Yani:
     * Bir kullanıcının birçok kategorisi olabilir
     * Bir kategorinin tek kullanıcısı olabilir
     */
    /** Null for built-in/system categories, non-null for user-created categories. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean active = true;

    //Category types must be equvilant to transaction types
    public enum CategoryType {
        EXPENSE,
        INCOME
    }

}
