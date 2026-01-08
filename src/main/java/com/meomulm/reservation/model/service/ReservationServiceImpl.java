package com.meomulm.reservation.model.service;

import com.meomulm.payment.model.mapper.PaymentMapper;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final PaymentMapper paymentMapper;

    @Override
    public void addReservation(Reservation reservation) {
        reservationMapper.insertReservation(reservation);
    }

    @Override
    public void updateReservation(Reservation reservation) {
        reservationMapper.updateReservation(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(int reservationId, int loginUserId) {
        /*

         */
        reservationMapper.deleteReservation(reservationId);
        paymentMapper.deletePayment(reservationId);
    }

    /*
    체크아웃 이후 스케줄러로 예약 상태 자동으로 수정하기
    reservationMapper.updateStatusToUsed(reservationId);
     */

}
