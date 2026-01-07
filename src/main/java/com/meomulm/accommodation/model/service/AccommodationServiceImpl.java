package com.meomulm.accommodation.model.service;

import com.meomulm.accommodation.model.dto.AccommodationDetail;
import com.meomulm.accommodation.model.dto.AccommodationImage;
import com.meomulm.accommodation.model.dto.SearchAccommodationResponse;
import com.meomulm.accommodation.model.mapper.AccommodationMapper;
import com.meomulm.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationMapper accommodationMapper;

    // 숙소 아이디를 기반으로 이미지 리스트 반환
    @Override
    public List<AccommodationImage> getAccommodationImagesById(int accommodationId) {
        List<AccommodationImage> accommodationImages = accommodationMapper.selectAccommodationImagesById(accommodationId);

        return accommodationImages;
    }

    // 이미 존재하는 List 안의 객체들을 set 하는 역할
    private void setAccommodationImages(List<SearchAccommodationResponse> responses) {
        for (SearchAccommodationResponse response : responses) {
            response.setAccommodationImages(
                    getAccommodationImagesById(response.getAccommodationId())
            );
        }
    }

    // 돋보기 검색 - 숙소명, 지역명으로 숙소 검색
    @Override
    public List<SearchAccommodationResponse> getAccommodationByKeyword(String keyword) {
        log.info("💡 숙소명, 지역명 숙소 검색 시작 - keyword={}", keyword);

        List<SearchAccommodationResponse> searchAccommodationResponse = accommodationMapper.selectAccommodationByKeyword(keyword);
        if (searchAccommodationResponse == null || searchAccommodationResponse.isEmpty()) {
            log.warn("❌ 숙소명, 지역명 숙소 검색 결과 없음 - keyword={}", keyword);
            throw new NotFoundException("해당 숙소가 존재하지 않습니다.");
        }

        setAccommodationImages(searchAccommodationResponse);
        log.info("✅ 숙소명, 지역명 숙소 검색 완료 - resultCount={}", searchAccommodationResponse.size());

        return searchAccommodationResponse;
    }

    // 지역 별 가격 낮은 순 12개
    @Override
    public List<SearchAccommodationResponse> getAccommodationPopularByAddress(String accommodationAddress) {
        log.info("💡 지역 별 가격 낮은 순 숙소 12개 검색 시작 - accommodationAddress={}", accommodationAddress);

        List<SearchAccommodationResponse> searchAccommodationResponse = accommodationMapper.selectAccommodationPopularByAddress(accommodationAddress);
        if (searchAccommodationResponse == null || searchAccommodationResponse.isEmpty()) {
            log.warn("❌ 지역 별 가격 낮은 순 숙소 12개 검색 결과 없음 - accommodationAddress={}", accommodationAddress);
            throw new NotFoundException("해당 지역 숙소가 존재하지 않습니다.");
        }

        setAccommodationImages(searchAccommodationResponse);
        log.info("✅ 지역 별 가격 낮은 순 숙소 12개 숙소 검색 완료 - resultCount={}", searchAccommodationResponse.size());

        return searchAccommodationResponse;
    }

    // 지도 클릭 -> 현재 위치 기반의 반경 5KM 숙소를 지도에 노출
    @Override
    public List<SearchAccommodationResponse> getAccommodationByLocation(double accommodationLatitude, double accommodationLongitude) {
        log.info("💡 지도 5km 반경 숙소 검색 시작 - latitude={}, longitude={}", accommodationLatitude, accommodationLongitude);


        List<SearchAccommodationResponse> searchAccommodationResponse = accommodationMapper.selectAccommodationByLocation(accommodationLatitude, accommodationLongitude);

        if (searchAccommodationResponse == null || searchAccommodationResponse.isEmpty()) {
            log.warn("❌ 지도 5km 반경 숙소 검색 결과 없음 - latitude={}, longitude={}", accommodationLatitude, accommodationLongitude);
            throw new NotFoundException("현재 위치 5km 내에 숙소가 존재하지 않습니다.");
        }

        setAccommodationImages(searchAccommodationResponse);
        log.info("✅ 지도 5km 반경 숙소 검색 완료 - resultCount={}", searchAccommodationResponse.size());

        return searchAccommodationResponse;
    }

    // 숙소 상세 검색
    @Override
    public AccommodationDetail getAccommodationDetailById(int accommodationId) {
        log.info("💡 숙소 상세 검색 시작 - accommodationId={}", accommodationId);

        AccommodationDetail accommodationDetail = accommodationMapper.selectAccommodationDetailById(accommodationId);
        if (accommodationDetail == null) {
            log.warn("❌ 숙소 상세 검색 결과 없음 - accommodationId={}", accommodationId);
            throw new NotFoundException("숙소 상세 검색이 존재하지 않습니다.");
        }

        getAccommodationImagesById(accommodationDetail.getAccommodationId());

        log.info("✅ 숙소 상세 검색 완료 - result={}", accommodationDetail.getAccommodationName());

        return accommodationDetail;
    }
}
