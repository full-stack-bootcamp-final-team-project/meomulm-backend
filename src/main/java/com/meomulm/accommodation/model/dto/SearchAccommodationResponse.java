package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAccommodationResponse {
    // 숙소 아이디
    private int accommodationId;
    // 숙소명
    private String accommodationName;
    // 숙소 주소
    private String accommodationAddress;
    // 위도
    private double accommodationLatitude;
    // 경도
    private double accommodationLongitude;
    // 객실 최소 가격
    private int minPrice;

    // 숙소 이미지 리스트
    private List<AccommodationImage> accommodationImages;
}


/*
[
  {
    "accommodationId": 127,
    "accommodationName": "스테이 더 베이크",
    "accommodationAddress": "제주특별자치도 제주시 조천읍 신북로 436",
    "accommodationLatitude": 33.512345,
    "accommodationLongitude": 126.785678,
    "minPrice": 89000,
    "accommodationImages": [
      {
        "imageId": 451,
        "imageUrl": "https://cdn.example.com/accommodations/127/main.jpg",
        "isMain": true,
        "order": 1
      },
      {
        "imageId": 452,
        "imageUrl": "https://cdn.example.com/accommodations/127/view1.jpg",
        "isMain": false,
        "order": 2
      },
      {
        "imageId": 453,
        "imageUrl": "https://cdn.example.com/accommodations/127/view2.jpg",
        "isMain": false,
        "order": 3
      }
    ]
  },
  {
    "accommodationId": 89,
    "accommodationName": "제주신화월드 호텔 앤 리조트",
    "accommodationAddress": "제주특별자치도 서귀포시 안덕면 신화역사로 304번길 38",
    "accommodationLatitude": 33.305678,
    "accommodationLongitude": 126.318912,
    "minPrice": 145000,
    "accommodationImages": [
      {
        "imageId": 301,
        "imageUrl": "https://cdn.example.com/accommodations/89/main.jpg",
        "isMain": true,
        "order": 1
      }
    ]
  }
]
 */