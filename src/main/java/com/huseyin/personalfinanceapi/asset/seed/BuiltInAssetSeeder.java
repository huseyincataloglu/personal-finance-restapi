package com.huseyin.personalfinanceapi.asset.seed;

import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.asset.repository.AssetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class BuiltInAssetSeeder implements CommandLineRunner {

    private AssetRepository assetRepository;

    public BuiltInAssetSeeder(
            AssetRepository assetRepository
    ){
        this.assetRepository = assetRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // --- KIYMETLİ MADENLER (PRECIOUS_METAL) ---
        // Küresel standart ons varlıkları (USD bazlı)
        uploadBuiltInAsset("Altın (Ons)",  "USD", "PRECIOUS_METAL");
        uploadBuiltInAsset("Gümüş (Ons)",  "USD", "PRECIOUS_METAL");

        // Yerel gram bazlı varlıklar (TRY bazlı)
        uploadBuiltInAsset("Gram Altın", "TRY", "PRECIOUS_METAL");
        uploadBuiltInAsset("Gram Gümüş",  "TRY", "PRECIOUS_METAL");

        // İstediğiniz özel yerel altın türleri (Adet ve TRY bazlı)
        uploadBuiltInAsset("Çeyrek Altın",  "TRY", "PRECIOUS_METAL");
        uploadBuiltInAsset("Tam Altın", "TRY", "PRECIOUS_METAL");

        // --- NAKİT / DÖVİZLER (FIAT_CURRENCY) ---
        uploadBuiltInAsset("Türk Lirası",  "TRY", "FIAT_CURRENCY");
        uploadBuiltInAsset("Amerikan Doları", "USD", "FIAT_CURRENCY");
        uploadBuiltInAsset("Euro", "EUR", "FIAT_CURRENCY");

        // --- KRİPTO PARALAR (CRYPTO) ---
        uploadBuiltInAsset("Bitcoin", "USD", "CRYPTO");
        uploadBuiltInAsset("Ethereum",  "USD", "CRYPTO");

        // --- POPÜLER HİSSE SENETLERİ (STOCK) ---
        // Borsa İstanbul (BIST) Örnekleri
        uploadBuiltInAsset("Türk Hava Yolları",  "TRY", "STOCK");
        uploadBuiltInAsset("Ereğli Demir Çelik",  "TRY", "STOCK");
        // Amerikan Borsası (NASDAQ/NYSE) Örnekleri
        uploadBuiltInAsset("Apple Inc.",  "USD", "STOCK");
        uploadBuiltInAsset("NVIDIA Corporation", "USD", "STOCK");

        // --- EMTİALAR (COMMODITY) ---
        uploadBuiltInAsset("Brent Petrol", "USD", "COMMODITY");



    }

    private void uploadBuiltInAsset(
            String name,
            String quoteCurrency,
            String type
    ){

        Asset.AssetType assetType = Asset.AssetType.from(type);

        if(assetRepository.existsByNameAndCurrencyAndType(name,quoteCurrency, Asset.AssetType.from(type))){
            return;
        }

        Asset asset = new Asset();
        asset.setName(name);
        asset.setCurrency(quoteCurrency);
        asset.setType(assetType);
        asset.setBuiltIn(true);
        assetRepository.save(asset);

    }

}
