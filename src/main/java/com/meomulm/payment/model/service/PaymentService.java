package com.meomulm.payment.model.service;

import com.meomulm.payment.model.dto.Payment;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {
    /**
     * 결제정보 추가
     * @param payment 결제 DTO
     * @param reservationId 예약 ID
     */
    void postPayment(Payment payment, int reservationId);
}