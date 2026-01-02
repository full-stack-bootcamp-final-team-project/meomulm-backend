package com.meomulm.review.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.review.model.dto.Review;
import com.meomulm.review.model.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthUtil authUtil;

    /**
     * 숙소별 리뷰 조회
     */
    @GetMapping("/accommodationId/{accommodationId}")
    public ResponseEntity<List<AccommodationReview>> getReviewByAccommodationId(@PathVariable int accommodationId) {
        List<AccommodationReview> reviews = reviewService.getReviewByAccommodationId(accommodationId);

        return ResponseEntity.ok(reviews);
    }

    /**
     * 내 리뷰 조회
     */
    @GetMapping("/userId")
    public ResponseEntity<List<MyReview>> getReviewByUserId(@RequestHeader(value = "Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        List<MyReview> reviews = reviewService.getReviewByUserId(currentUserId);

        return ResponseEntity.ok(reviews);
    }

    /**
     * 리뷰 작성
     */
    @PostMapping
    public ResponseEntity<Void> postReview(@RequestHeader(value = "Authorization") String authHeader, @RequestBody Review review) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);

        reviewService.postReview(
                currentUserId,
                review.getAccommodationId(),
                review.getRating(),
                review.getReviewContent()
        );

        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@RequestHeader(value = "Authorization") String authHeader, @PathVariable int reviewId) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        reviewService.deleteReview(reviewId, currentUserId);

        return ResponseEntity.ok().build();
    }
}
