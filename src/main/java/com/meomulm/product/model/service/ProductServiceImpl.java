package com.meomulm.product.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.product.model.dto.Product;
import com.meomulm.product.model.dto.ProductFacility;
import com.meomulm.product.model.dto.ProductImage;
import com.meomulm.product.model.dto.ProductResponse;
import com.meomulm.product.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

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
                "has_tv": true,
                "has_bath: true,
                 ...
                 },
          "images": [
                {
                  "productImageId": 1,
                  "productImageUrl": "url1"
                },
                {
                  "productImageId": 2,
                  "productImageUrl": "url2"
                }
            ]
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
                "has_bath: true,
                 ...
                 },
          "images": [
                {
                  "productImageId": 13,
                  "productImageUrl": "url13"
                },
                {
                  "productImageId": 14,
                  "productImageUrl": "url14"
                }
            ]
        },
      "availableProductIds": [1, 3, 5]   // 해당 기간에 예약 가능한 product_id 배열
    }
     */

    @Override
    public ProductResponse getRoomsByAccommodationId(int accommodationId, String checkInDate, String checkOutDate, int guestCount) {
        /*
        숙소 상세 페이지에서 "모든 객실 보기" 버튼 클릭 시
        1. Product 테이블에서 해당 숙소 ID를 기준으로 조회한 객실 정보를 배열로 저장한다.
            조회되는 값이 없으면 빈 배열을 반환한다.
        2. product_facility 테이블에서 객실 ID를 기준으로 조회한 결과를 저장한다.
        3. product_image 테이블에서 객실 ID를 기준으로 조회한 이미지 결과를 배열로 저장한다.
        4. 날짜와 인원 수를 조건으로 예약 가능 여부 확인 후 배열에 저장한다.
         */
        if (accommodationId <= 0) {
            throw new BadRequestException("유효하지 않은 숙소 ID입니다.");
        }
        if (checkInDate == null || checkInDate.isBlank()
                || checkOutDate == null || checkOutDate.isBlank()) {
            throw new BadRequestException("체크인과 체크아웃 날짜는 필수입니다.");
        }
        if (checkInDate.equals(checkOutDate)) {
            throw new BadRequestException("체크인과 체크아웃 날짜는 같을 수 없습니다.");
        }
        if (guestCount <= 0) {
            throw new BadRequestException("인원수는 1명 이상이어야 합니다.");
        }
        List<Product> productList = productMapper.selectRoomsByAccommodationId(accommodationId);
        if (productList == null || productList.isEmpty()) {
            throw new NotFoundException("해당 숙소에 등록된 객실이 없습니다.");
        }
        for (Product product : productList) {
            int productId = product.getProductId();
            ProductFacility facility = productMapper.selectProductFacilityByProductId(productId);
            product.setFacility(facility);
            List<ProductImage> images = productMapper.selectProductImagesByProductId(productId);
            if (images == null) {
                images = Collections.emptyList();
            }
            product.setImages(images);
        }
        List<Integer> availableProductId = productMapper.selectAvailableProductId(
                accommodationId, checkInDate, checkOutDate, guestCount);
        if (availableProductId == null) {
            availableProductId = Collections.emptyList();
        }
        return new ProductResponse(productList, availableProductId);
    }
}