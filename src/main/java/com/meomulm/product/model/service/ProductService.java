package com.meomulm.product.model.service;

import com.meomulm.product.model.dto.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
public interface ProductService {
    List<Product> getRoomsByAccommodationId(int accommodationId);
//    Product getRoomByProductId(int productId);

}
