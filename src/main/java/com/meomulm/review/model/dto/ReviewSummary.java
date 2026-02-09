package com.meomulm.review.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummary {
    // 평균 평점
    private double averageRating;
    // 총 리뷰 개수
    private int totalCount;
    // 가장 최근 리뷰 내용
    private String latestContent;
}