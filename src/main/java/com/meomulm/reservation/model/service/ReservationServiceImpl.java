package com.meomulm.reservation.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.ForbiddenException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.common.exception.UnauthorizedException;
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

    private boolean isNotExist(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String regex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(regex);
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        // 010-1234-5678 또는 01012345678
        String regex = "^010-?\\d{4}-?\\d{4}$";
        return phone.matches(regex);
    }

    private String changePhoneForm(String phone) {
        if (phone == null) return null;
        return phone.replace("-", "");
    }


    @Override
    public List<Reservation> getAllReservationsByUserId(int loginUserId) {
        List<Reservation> reservations = reservationMapper.selectAllReservationsByUserId(loginUserId);
        if (reservations.isEmpty()) {
            throw new NotFoundException("조회 결과가 없습니다.");
        }
        return reservations;
    }

    @Override
    public Reservation getReservationById(int reservationId, int loginUserId) {
        Reservation reservation = reservationMapper.selectReservationById(reservationId);
        if (reservation == null) {
            throw new NotFoundException("조회 결과가 없습니다.");
        }
        if (reservation.getUserId() != loginUserId) {
            throw new ForbiddenException("예약자 본인만 조회 가능합니다.");
        }
        return reservation;
    }

    @Override
    public void addReservation(Reservation reservation) {
        if (reservation == null) {
            throw new BadRequestException("예약 정보가 전달되지 않았습니다.");
        }
        if (isNotExist(reservation.getBookerName())
                || isNotExist(reservation.getBookerEmail())
                || isNotExist(reservation.getBookerPhone())) {
            throw new BadRequestException("예약자 정보는 필수입니다.");
        }
        if (!isValidEmail(reservation.getBookerEmail())) {
            throw new BadRequestException("유효하지 않은 이메일 형식입니다.");
        }
        if (!isValidPhone(reservation.getBookerPhone())) {
            throw new BadRequestException("연락처는 010-1234-5678 또는 01012345678 형식으로 입력해주세요.");
        }
        reservation.setBookerPhone(changePhoneForm(reservation.getBookerPhone()));
        reservationMapper.insertReservation(reservation);
    }

    @Override
    public void updateReservation(Reservation reservation, int loginUserId) {
        Reservation isExistReservation = reservationMapper.selectReservationById(reservation.getReservationId());
        if(isExistReservation == null) {
            throw new NotFoundException("수정하려는 예약을 찾을 수 없습니다.");
        }
        if(isExistReservation.getUserId() != loginUserId){
            throw new ForbiddenException("예약자 본인만 수정할 수 있습니다.");
        }
        reservationMapper.updateReservation(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Reservation reservation, int loginUserId) {
        Reservation isExistReservation = reservationMapper.selectReservationById(reservation.getReservationId());
        if(isExistReservation == null) {
            throw new NotFoundException("취소하려는 예약을 찾을 수 없습니다.");
        }
        if(isExistReservation.getUserId() != loginUserId){
            throw new ForbiddenException("예약자 본인만 취소할 수 있습니다.");
        }
        reservationMapper.deleteReservation(reservation.getReservationId());
        paymentMapper.deletePayment(reservation.getReservationId());
    }

    /*
    체크아웃 이후 스케줄러로 예약 상태 자동으로 수정하기
    reservationMapper.updateStatusToUsed(reservationId);
     */

}
