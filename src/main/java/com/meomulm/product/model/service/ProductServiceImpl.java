package com.meomulm.product.model.service;

import com.meomulm.product.model.dto.Product;
import com.meomulm.product.model.dto.ProductFacility;
import com.meomulm.product.model.dto.ProductImage;
import com.meomulm.product.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    final ProductMapper productMapper;

    @Override
    public List<Product> getRoomsByAccommodationId(int accommodationId) {
        /*
        숙소 상세 페이지에서 "모든 객실 보기" 버튼 클릭 시
        1. Product 테이블에서 해당 숙소 ID를 기준으로 조회한 객실 정보를 배열로 저장한다.
            조회되는 값이 없으면 빈 배열을 반환한다.
        2. product_facility 테이블에서 객실 ID를 기준으로 조회한 결과를 저장한다.
        3. product_image 테이블에서 객실 ID를 기준으로 조회한 이미지 결과를 배열로 저장한다.
        4.
         */
        try{
            log.info("GET accommodationId : {}", accommodationId);
            List<Product> productListRes = productMapper.selectRoomsByAccommodationId(accommodationId);
            if (productListRes.isEmpty()) {
                log.info("숙소 ID {}에 해당하는 객실이 없습니다.", accommodationId);
                return List.of();
            }
            for (Product product : productListRes) {
                int productId = product.getProductId();
                // 객실 시설 정보 조회
                ProductFacility facility = productMapper.selectProductFacilityByProductId(productId);
                product.setFacility(facility); // Product 객체에 시설 정보 주입
                // 객실 이미지 목록 조회
                List<ProductImage> images = productMapper.selectProductImagesByProductId(productId);
                product.setImages(images); // Product 객체에 이미지 리스트 주입
            }
            return productListRes;
        } catch (Exception e) {
            log.error("getRoomsByAccommodationId 조회 실패 : ", e);
//            throw new
        }
        return List.of();
    }
}


/*
{
  "allProducts": [
    {
      "product_id": 1,
      "product_name": "스탠다드",
      "proudct_price": 120000,
      "product_standard_number": 2,
      "product_maximum_number": 4,
      "product_count": 5,               // 총 재고 수
      "facilities": {
            "has_tv": true, ...},
      "images": ["url1", "url2", ...],
      "images": {
            "main_image": ...,
            "sub_image_1": ...,
             ...
       }
    },
    {
      "product_id": 2,
      "product_name": "스위트룸",
      "proudct_price": 185000,
      "product_standard_number": 2,
      "product_maximum_number": 4,
      "product_count": 7,               // 총 재고 수
      "facilities": {
            "has_tv": true,
             ...
       },
      "images": ["url13", "url14", ...],
      "images": {
            "main_image": ...,
            "sub_image_1": ...,
             ...
       }
    },
    ...
  ],
  "availableProductIds": [1, 3, 5]   // 해당 기간에 예약 가능한 product_id 배열
}
 */