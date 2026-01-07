package com.meomulm.payment.model.service;

import com.meomulm.payment.model.dto.Payment;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {
    void addPayment(Payment payment);
}
