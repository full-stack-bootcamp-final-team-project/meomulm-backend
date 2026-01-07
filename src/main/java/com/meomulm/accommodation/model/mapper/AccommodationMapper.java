package com.meomulm.accommodation.model.mapper;


import com.meomulm.accommodation.model.dto.AccommodationDetail;
import com.meomulm.accommodation.model.dto.AccommodationImage;
import com.meomulm.accommodation.model.dto.SearchAccommodationResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccommodationMapper {
    // 숙소 아이디를 기반으로 이미지 리스트 반환
    List<AccommodationImage> selectAccommodationImagesById(int accommodationId);

    // 돋보기 검색 - 숙소명, 지역명으로 숙소 검색
    List<SearchAccommodationResponse> selectAccommodationByKeyword(String keyword);

    // 지역 별 가격 낮은 순 12개
    List<SearchAccommodationResponse> selectAccommodationPopularByAddress(String accommodationAddress);

    // 지도 클릭 -> 현재 위치 기반의 반경 5KM 숙소를 지도에 노출
    List<SearchAccommodationResponse> selectAccommodationByLocation(double accommodationLatitude, double accommodationLongitude);

    // 숙소 상세 검색
    AccommodationDetail selectAccommodationDetailById(int accommodationId);
}
