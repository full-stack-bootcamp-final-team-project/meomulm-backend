package com.meomulm.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// === 내부 DTO 클래스 정의 ===
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFacility {
    private int productFacilityId ;
    private int productId ;
    private boolean hasBath ;
    private boolean hasAirCondition ;
    private boolean hasRefrigerator ;
    private boolean hasBidet ;
    private boolean hasTv ;
    private boolean hasPc ;
    private boolean hasInternet ;
    private boolean hasToiletries ;
}