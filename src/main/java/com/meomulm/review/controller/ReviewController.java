package com.meomulm.review.controller;

import com.meomulm.review.model.dto.AccommodationReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    @GetMapping("/accommodationId/{accommodationId}")
    public ResponseEntity<List<AccommodationReview>> getReviewByAccommodationId (@PathVariable int accommodationId){


    }

}
