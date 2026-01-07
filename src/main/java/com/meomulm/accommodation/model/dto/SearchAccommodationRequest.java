package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAccommodationRequest {
    // 숙소 검색 키워드
    private String keyword;
    // 숙소 주소
    private String accommodationAddress;
    // 경도
    private double longitude;
    // 위도
    private double latitude;
}