package com.huseyin.personalfinanceapi.asset.controller;


import com.huseyin.personalfinanceapi.asset.controller.dto.CreateAssetRequest;
import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.asset.service.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(
            AssetService assetService
    ){
        this.assetService = assetService;
    }

    @GetMapping
    public ResponseEntity<List<Asset>> getAll(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "true") boolean all
    ){
        return ResponseEntity
                .ok(assetService.getAllForUser(userId,all));

    }

    @PostMapping
    public ResponseEntity<Asset> createAsset(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateAssetRequest request
            ){

        Asset asset = assetService.create(userId,request);
        return ResponseEntity.created(
                URI.create("/assets/{%s}".formatted(asset.getId()))
        ).body(asset);


    }




}
