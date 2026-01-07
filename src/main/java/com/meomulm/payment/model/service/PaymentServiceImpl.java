package com.meomulm.payment.model.service;

import com.meomulm.payment.model.dto.Payment;
import com.meomulm.payment.model.mapper.PaymentMapper;
import com.meomulm.reservation.model.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final ReservationMapper reservationMapper;

    @Override
    public void addPayment(Payment payment) {
        paymentMapper.insertPayment(payment);
        reservationMapper.updateStatusToPaid(payment.getReservation_id());
    }
}
