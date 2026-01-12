package com.meomulm.product.model.service;

import com.meomulm.product.model.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public interface ProductService {
    ProductResponse getRoomsByAccommodationId(int accommodationId, String checkInDate, String checkOutDate, int guestCount);
//    Product getRoomByProductId(int productId);

}