package com.huseyin.personalfinanceapi.transaction.resolver;

import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.asset.repository.AssetRepository;
import com.huseyin.personalfinanceapi.common.exception.ResourceAccesDeniedException;
import com.huseyin.personalfinanceapi.common.exception.ResourceNotFoundException;
import com.huseyin.personalfinanceapi.transaction.controller.dto.asset.OpeningAssetRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Asset> resolveAssets(
            Long userId,
            List<OpeningAssetRequest.AssetItemLine> assetItemLineList
    ){
        //Create a set by eliminating automatically duplicated asset ids
        Set<Long> requestAssetIds = assetItemLineList.stream().map(
                        OpeningAssetRequest.AssetItemLine::assetId)
                .collect(Collectors.toSet());

        //Validate that the assests in the request exist
        List<Asset > assetsFromDb  =assetRepository.findAllById(requestAssetIds);
        if(assetsFromDb.size() != requestAssetIds.size()) {
            throw new ResourceNotFoundException("There is unidentified resource in the request.");
        }
        //Validate if the non-builtin assest belong to the user
        boolean hasUnauthorizedAsset = assetsFromDb.stream().filter(it -> !it.isBuiltIn())
                .anyMatch(it -> !it.getUser().getId().equals(userId));

        if(hasUnauthorizedAsset){
            throw new ResourceAccesDeniedException("Resource access denied exception");
        }

        return assetsFromDb;

    }





}
