package com.meomulm.product.model.mapper;


import com.meomulm.product.model.dto.Product;
import com.meomulm.product.model.dto.ProductFacility;
import com.meomulm.product.model.dto.ProductImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 숙소 ID로 객실 목록 조회
    List<Product> selectRoomsByAccommodationId(int accommodationId);

    // 객실 ID로 객실별 정보 조회
    ProductFacility selectProductFacilityByProductId(int productId);

    // 객실 ID로 객실별 이미지 조회
    List<ProductImage> selectProductImagesByProductId(int productId);

//    List<ProductDetailMap> selectRoomsWithDetails

}