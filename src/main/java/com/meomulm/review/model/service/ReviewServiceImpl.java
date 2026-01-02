package com.meomulm.review.model.service;

import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.review.model.dto.Review;
import com.meomulm.review.model.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    @Override
    public List<AccommodationReview> getReviewByAccommodationId(int accommodationId) {
        try {
            return reviewMapper.selectReviewByAccommodationId(accommodationId);
        } catch (Exception e) {
            log.error("숙소 리뷰 조회 실패 - accommodationId={}", accommodationId, e);
            throw e;
        }
    }

    @Override
    public List<MyReview> getReviewByUserId(int userId) {
        try {
            return reviewMapper.selectReviewByUserId(userId);
        } catch (Exception e) {
            log.error("유저 리뷰 조회 실패 - userId={}", userId, e);
            throw e;
        }
    }

    @Override
    public void postReview(int userId, int accommodationId, int rating, String reviewContent) {
        Review review = new Review();
        review.setUserId(userId);
        review.setAccommodationId(accommodationId);
        review.setRating(rating);
        review.setReviewContent(reviewContent);

        try {
            reviewMapper.insertReview(review);
        } catch (Exception e) {
            log.error("리뷰 등록 실패 - userId={}, accommodationId={}", userId, accommodationId, e);
            throw e;
        }
    }

    @Override
    public void deleteReview(int reviewId, int userId) {
        Review review = new Review();
        review.setReviewId(reviewId);
        review.setUserId(userId);

        try {
            reviewMapper.deleteReview(review);
        } catch (Exception e) {
            log.error("리뷰 삭제 실패 - reviewId={}, userId={}", reviewId, userId, e);
            throw e;
        }
    }
}
