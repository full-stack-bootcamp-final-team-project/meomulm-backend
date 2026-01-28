package com.meomulm.accommodation.controller;

import com.meomulm.accommodation.model.dto.AccommodationDetail;
import com.meomulm.accommodation.model.dto.SearchAccommodationRequest;
import com.meomulm.accommodation.model.dto.SearchAccommodationResponse;
import com.meomulm.accommodation.model.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodation")
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {
    private final AccommodationService accommodationService;

    /**
     * 키워드로 숙소 조회
     * @param keyword 숙소검색 요청 DTO
     * @return 숙소검색 응답 DTO 리스트 + 상태코드 200
     */
    @GetMapping("/keyword")
    public ResponseEntity<List<SearchAccommodationResponse>> getAccommodationByKeyword(
            @RequestParam String keyword) {
        log.info("🔥 Controller 진입 - keyword={}", keyword);
        List<SearchAccommodationResponse> searchAccommodationResponse =
                accommodationService.getAccommodationByKeyword(keyword);
        return ResponseEntity.ok(searchAccommodationResponse);
    }

    /**
     * 지역별 가격 낮은 숙소 12개 조회
     * @param request 숙소검색 요청 DTO
     * @return 숙소검색 응답 DTO 리스트 + 상태코드 200
     */
    @GetMapping("/popular")
    public ResponseEntity<List<SearchAccommodationResponse>> getAccommodationPopularByAddress(
            @RequestBody SearchAccommodationRequest request) {
        log.info("🔥 Controller 진입 - accommodationAddress={}",
                request.getAccommodationAddress());
        List<SearchAccommodationResponse> searchAccommodationResponse =
                accommodationService.getAccommodationPopularByAddress(
                        request.getAccommodationAddress());
        return ResponseEntity.ok(searchAccommodationResponse);
    }

    /**
     * 현재위치 기반 반경 5km 내 숙소 조회 : 지도 검색
     * @param request 숙소검색 요청 DTO
     * @return 숙소검색 응답 DTO 리스트 + 상태코드 200
     */
    @GetMapping("/map")
    public ResponseEntity<List<SearchAccommodationResponse>> getAccommodationByLocation(
            @RequestBody SearchAccommodationRequest request) {
        log.info("🔥 Controller 진입 - location={},{}",
                request.getLatitude(),
                request.getLongitude());
        List<SearchAccommodationResponse> searchAccommodationResponse =
                accommodationService.getAccommodationByLocation(
                        request.getLatitude(),
                        request.getLongitude());
        return ResponseEntity.ok(searchAccommodationResponse);
    }

    /**
     * 숙소 ID로 숙소 상세정보 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 상세정보 DTO + 상태코드 200
     */
    @GetMapping("/detail/{accommodationId}")
    public ResponseEntity<AccommodationDetail> getAccommodationDetailById(
            @PathVariable int accommodationId) {
        log.info("🔥 Controller 진입 - accommodationId={}",
                accommodationId);
        AccommodationDetail accommodationDetail =
                accommodationService.getAccommodationDetailById(
                        accommodationId);
        return ResponseEntity.ok(accommodationDetail);
    }

}
