package com.meomulm.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int productId;
    private int accommodationId;
    private String productName;
    private String productCheckInTime;
    private String productCheckOutTime;
    private int productPrice;
    private int productStandardNumber;
    private int productMaximumNumber;
    private int productCount;

    private ProductFacility facility;           // 1:1
    private List<ProductImage> images;          // 1:N
}
