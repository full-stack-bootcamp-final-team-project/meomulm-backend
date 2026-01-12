package com.meomulm.payment.controller;

import com.meomulm.common.exception.UnauthorizedException;
import com.meomulm.common.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    /*
    예약 페이지에서 결제 페이지 이동 시
    예약Id 전달받아서 사용할 것
     */
    @PutMapping("/{reservationId}")
    public ResponseEntity<Void> addPayment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Payment payment,
            @PathVariable int reservationId) {
        String token = verifyToken(authHeader);
        paymentService.addPayment(payment, reservationId);
        return ResponseEntity.ok().build();
    }

    /*
    Bearer 토큰 유효성 검사
    예외 처리 또는 사용자 ID 추출
     */
    private String verifyToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("인증 헤더가 없거나 잘못되었습니다.");
        }
        return authHeader.substring(7);
    }
}