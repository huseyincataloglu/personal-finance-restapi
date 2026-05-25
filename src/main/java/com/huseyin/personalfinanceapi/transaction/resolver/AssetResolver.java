package com.huseyin.personalfinanceapi.transaction.resolver;

import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.asset.repository.AssetRepository;
import com.huseyin.personalfinanceapi.asset.service.AssetService;
import com.huseyin.personalfinanceapi.common.exception.ResourceAccesDeniedException;
import com.huseyin.personalfinanceapi.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AssetResolver {

    private final AssetRepository assetRepository;


    public AssetResolver(AssetRepository assetRepository) {

        this.assetRepository = assetRepository;
    }


    public Asset resolveAsset(
            Long userId,
            Long assetId
    ){
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset with id:"+assetId + "does not exists"));

        if(!asset.getUser().getId().equals(userId)){
            throw new ResourceAccesDeniedException("Asset with id:" + assetId + "belongs to somebody else.");
        }

        return asset;


    }





}
