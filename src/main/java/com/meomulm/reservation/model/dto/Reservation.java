package com.meomulm.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    private int reservationId;      // 예약 ID
    private int userId;             // 유저 ID
    private int productId;          // 객실 ID
    private String bookerName;      // 예약자명
    private String bookerEmail;     // 예약자 이메일
    private String bookerPhone;     // 예약자 전화번호
    private String checkInDate;     // 체크인 날짜
    private String checkOutDate;    // 체크아웃 날짜
    private int guestCount;         // 예약 인원 수
    private String status;          // 예약 상태
    private int totalPrice;         // 총 가격
    private String createdAt;       // 생성일자
    private String updatedAt;       // 변경일자
}
