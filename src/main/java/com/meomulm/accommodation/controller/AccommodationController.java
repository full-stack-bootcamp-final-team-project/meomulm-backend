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
import java.util.Map;

@RestController
@RequestMapping("/api/accommodation")
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {
    private final AccommodationService accommodationService;

    // http://localhost:8080/api/accommodation/keyword
    // {
    //    "keyword": "제주"
    // }
    @GetMapping("/keyword")
    public ResponseEntity<List<SearchAccommodationResponse>> getAccommodationByKeyword(@RequestBody SearchAccommodationRequest request) {
        log.info("🔥 Controller 진입 - keyword={}", request.getKeyword());
        List<SearchAccommodationResponse> searchAccommodationResponse = accommodationService.getAccommodationByKeyword(request.getKeyword());
        return ResponseEntity.ok(searchAccommodationResponse);
    }

    // http://localhost:8080/api/accommodation/popular
    // {
    //    "accommodationAddress": "제주"
    // }
    @GetMapping("/popular")
    public ResponseEntity<List<SearchAccommodationResponse>> getAccommodationPopularByAddress(@RequestBody SearchAccommodationRequest request) {
        log.info("🔥 Controller 진입 - accommodationAddress={}", request.getAccommodationAddress());
        List<SearchAccommodationResponse> searchAccommodationResponse = accommodationService.getAccommodationPopularByAddress(request.getAccommodationAddress());
        return ResponseEntity.ok(searchAccommodationResponse);
    }

    // http://localhost:8080/api/accommodation/map
    // {
    //    "latitude": 33.4629,
    //    "longitude": 126.3095
    // }
    @GetMapping("/map")
    public ResponseEntity<List<SearchAccommodationResponse>> getAccommodationByLocation(@RequestBody SearchAccommodationRequest request) {
        log.info("🔥 Controller 진입 - location={},{}", request.getLatitude(), request.getLongitude());
        List<SearchAccommodationResponse> searchAccommodationResponse = accommodationService.getAccommodationByLocation(request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok(searchAccommodationResponse);
    }

    // http://localhost:8080/api/accommodation/detail/1
    @GetMapping("/detail/{accommodationId}")
    public ResponseEntity<AccommodationDetail> getAccommodationDetailById(@PathVariable int accommodationId) {
        log.info("🔥 Controller 진입 - accommodationId={}", accommodationId);
        AccommodationDetail accommodationDetail = accommodationService.getAccommodationDetailById(accommodationId);

        return ResponseEntity.ok(accommodationDetail);
    }

}
