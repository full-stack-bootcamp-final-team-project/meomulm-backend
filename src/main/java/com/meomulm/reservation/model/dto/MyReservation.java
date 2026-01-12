package com.meomulm.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyReservation {
    // vw_mypage_reservation 사용하는 DTO
    private int accommodationId;            // 숙소 ID
    private String accommodationName;       // 숙소명
    private int productId;                  // 객실 ID
    private String productName;             // 객실명
    private String productCheckInTime;      // 입실시간
    private String productCheckOutTime;     // 퇴실시간
    private int reservationId;              // 예약 ID
    private int userId;                     // 유저 ID
    private String checkInDate;             // 체크인 날짜
    private String checkOutDate;            // 체크아웃 날짜
    private String status;                  // 예약 상태
}