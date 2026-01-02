package com.meomulm.review.model.mapper;


import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.review.model.dto.Review;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<AccommodationReview> selectReviewByAccommodationId(int accommodationId);
    List<MyReview> selectReviewByUserId(int userId);
    void insertReview(Review review);
    void deleteReview(Review review);
}
