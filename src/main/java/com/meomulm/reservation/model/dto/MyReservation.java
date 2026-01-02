package com.meomulm.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyReservation {
    // vw_mypage_reservation 사용하는 DTO

    // 숙소 ID
    int accommodationId;
    // 숙소명
    String accommodationName;

    // 객실 ID
    int productId;
    // 객실명
    String productName;
    // 입실시간
    String productCheckInTime;
    // 퇴실시간
    String productCheckOutTime;

    // 예약 ID
    int reservationId;
    // 유저 ID
    int userId;
    // 체크인 날짜
    String checkInDate;
    // 체크아웃 날짜
    String checkOutDate;
    // 예약 상태
    String status;
}
