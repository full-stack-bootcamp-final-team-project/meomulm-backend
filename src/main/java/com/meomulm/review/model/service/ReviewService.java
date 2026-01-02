package com.meomulm.review.model.service;

import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;

import java.util.List;

public interface ReviewService {
    // 숙소별 리뷰들 조회
    List<AccommodationReview> getReviewByAccommodationId(int accommodationId);
    // 본인의 리뷰들 조회
    List<MyReview> getReviewByUserId(int userId);
    // 리뷰 생성
    void postReview(int userId, int accommodationId, int rating, String reviewContent);
    // 리뷰 삭제
    void deleteReview(int reviewId, int userId);
}
