package com.meomulm.product.model.service;

import com.meomulm.product.model.dto.Product;
import com.meomulm.product.model.dto.ProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
public interface ProductService {
    ProductResponse getRoomsByAccommodationId(int accommodationId, String checkInDate, String checkOutDate);
//    Product getRoomByProductId(int productId);

}