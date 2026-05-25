package com.huseyin.personalfinanceapi.asset.entity;


import com.huseyin.personalfinanceapi.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "asset",
uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","name", "quote_currency","type"}))
@Getter
@Setter
@NoArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "built_in",nullable = false)
    private boolean isBuiltIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "quote_currency",nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetType type;

    public enum AssetType{
        STOCK,          // hisse senedi - THYAO, AAPL
        CRYPTO,         // kripto - BTC, ETH
        PRECIOUS_METAL, // altın, gümüş - XAU, XAG
        FUND,           // yatırım fonu
        BOND,           // tahvil, bono
        COMMODITY;       // emtia


        public static AssetType from(String value) {
            if (value == null) {
                return null;
            }

            return Arrays.stream(AssetType.values())
                    .filter(it -> it.name().equalsIgnoreCase(value.trim()))
                    .findFirst()
                    .orElse(null); // Bulamazsa hata fırlatmak yerine null döner
        }



    }





}
