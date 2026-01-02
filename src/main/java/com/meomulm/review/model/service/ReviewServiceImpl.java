package com.meomulm.review.model.service;

import com.meomulm.common.exception.UnauthorizedException;
import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;
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
            reviewMapper.selectReviewByAccommodationId(accommodationId);
        } catch (Exception e) {

        }

        return List.of();
    }

    @Override
    public List<MyReview> getReviewByUserId(int userId) {
        try {
            reviewMapper.selectReviewByUserId(userId);
        } catch (Exception e) {

        }

        return List.of();
    }

    @Override
    public void postReview(int userId, int accommodationId, int rating, String reviewContent) {

        reviewMapper.insertReview();

    }

    @Override
    public void deleteReview(int reviewId, int userId) {
        reviewMapper.deleteReview();
    }
}
