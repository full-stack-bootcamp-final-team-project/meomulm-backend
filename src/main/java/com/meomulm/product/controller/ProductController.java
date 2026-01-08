package com.meomulm.product.controller;

import com.meomulm.product.model.dto.Product;
import com.meomulm.product.model.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    final ProductService productService;

    /*
    날짜 매개변수 추가
     */
    @GetMapping("/room/search/{accommodationId}")
    public ResponseEntity<List<Product>> getRoomsByAccommodationId(
            @PathVariable int accommodationId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate){
        List<Product> res = productService.getRoomsByAccommodationId(accommodationId, checkInDate, checkOutDate);
        return ResponseEntity.ok(res);
    }

//    @GetMapping("/room/detail/{productId}")
//    public ResponseEntity<Product> getRoomsByProductId(@PathVariable int productId){
//        try {
//            log.info("GET productId : ", productId);
//            Product res = productService.getRoomByProductId(productId);
//            return ResponseEntity.ok(res);
//        } catch (Exception e) {
//            log.error("getRoomByProductId 조회 실패 : ", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}