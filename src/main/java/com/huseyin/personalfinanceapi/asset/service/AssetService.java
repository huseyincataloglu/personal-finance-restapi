package com.huseyin.personalfinanceapi.asset.service;

import com.huseyin.personalfinanceapi.asset.controller.dto.CreateAssetRequest;
import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.asset.repository.AssetRepository;
import com.huseyin.personalfinanceapi.common.accessgate.UserAccessGate;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final UserAccessGate userAccessGate;

    public List<Asset> getAllForUser(
            Long userId,
            boolean all
    ){
        userAccessGate.requireFinancialAccess(userId);

        if(all){
            return assetRepository.findSystemAndUserAssets(userId);
        }
        else {
            return assetRepository.findOnlyUserCreatedAssets(userId);
        }


    }

    public Asset create(Long userId,CreateAssetRequest request) {

        User user = userAccessGate.requireFinancialAccess(userId);

        if(assetRepository
                .existsForSystemOrUser(
                       request.name(),
                        Asset.AssetType.from(request.assetType()),
                        request.currency(),
                        user.getId()
                ))
        {
            throw new BusinessRuleViolationException("The requested asset already exists and cannot be created again");
        }

        Asset asset = new Asset();
        asset.setUser(user);
        asset.setName(
                request.name().trim()
        );
        asset.setCurrency(request.currency().trim().toUpperCase());
        asset.setType(Asset.AssetType.from(request.assetType()));

        return assetRepository.save(asset);

    }



}
