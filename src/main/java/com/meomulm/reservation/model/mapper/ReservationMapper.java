package com.meomulm.reservation.model.mapper;


import com.meomulm.reservation.model.dto.Reservation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReservationMapper {
    // 예약 조회
    List<Reservation> selectAllReservationsByUserId(int loginUserId);
    // 예약 조회 (예약Id)
    Reservation selectReservationById(int reservationId);




    // 예약 추가
    void insertReservation(Reservation reservation);
    // 예약 수정
    void updateReservation(Reservation reservation);
    // 예약 상태 변경 (결제 후)
    void updateStatusToPaid(int reservationId);
    // 예약 상태 변경 (이용 후)
    void updateStatusToUsed(int reservationId);
    // 예약 취소 (상태 변경)
    void deleteReservation(int reservationId);
}