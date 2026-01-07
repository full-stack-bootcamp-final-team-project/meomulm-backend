package com.meomulm.payment.controller;

import com.meomulm.payment.model.dto.Payment;
import com.meomulm.payment.model.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PutMapping("/")
    public ResponseEntity<Void> addPayment(@RequestBody Payment payment) {
        paymentService.addPayment(payment);
        return ResponseEntity.ok().build();
    }
}
