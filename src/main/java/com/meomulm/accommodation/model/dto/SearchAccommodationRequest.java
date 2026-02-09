package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAccommodationRequest {

    // --- 기본 검색 파라미터 ---
    private String keyword;               // 숙소명 또는 지역명 키워드
    private String accommodationAddress;  // 인기 숙소 조회용 주소
    private Double longitude;             // 현위치 경도
    private Double latitude;              // 현위치 위도

    // --- 필터링 파라미터 (추가됨) ---
    private List<String> facilities;      // 편의시설 리스트 (has_parking, has_wifi 등)
    private List<String> types;           // 숙소 종류 리스트 (HOTEL, MOTEL 등)
    private Integer minPrice;             // 최소 가격
    private Integer maxPrice;             // 최대 가격

}