package com.meomulm.payment.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.payment.model.dto.Payment;
import com.meomulm.payment.model.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthUtil authUtil;

    /**
     * 결제정보 추가
     * @param authHeader    JWT 토큰 헤더
     * @param payment       결제 DTO
     * @param reservationId URL 경로에서 받은 예약 ID
     * @return 상태코드 200
     */
    @PostMapping("/{reservationId}")
    public ResponseEntity<Void> postPayment(@RequestHeader("Authorization") String authHeader, @RequestBody Payment payment, @PathVariable int reservationId) {
        authUtil.getCurrentUserId(authHeader);
        paymentService.postPayment(payment, reservationId);
        return ResponseEntity.ok().build();
    }
}