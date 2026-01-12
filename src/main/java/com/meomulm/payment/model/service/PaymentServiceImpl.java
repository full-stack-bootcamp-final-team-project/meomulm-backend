package com.meomulm.payment.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.payment.model.dto.Payment;
import com.meomulm.payment.model.mapper.PaymentMapper;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final ReservationMapper reservationMapper;

    /**
     * 결제정보 추가
     * @param payment 결제 DTO
     * @param reservationId 예약 ID
     */
    @Transactional
    @Override
    public void postPayment(Payment payment, int reservationId) {
        if (payment == null) {
            throw new BadRequestException("결제 정보가 전달되지 않았습니다.");
        }
        Reservation isExistReservation = reservationMapper.selectReservationById(reservationId);
        if(isExistReservation == null) {
            throw new NotFoundException("예약 정보를 찾을 수 없습니다.");
        }
        payment.setReservationId(reservationId);
        paymentMapper.insertPayment(payment);
        reservationMapper.updateStatusToPaid(payment.getReservationId());
    }
}