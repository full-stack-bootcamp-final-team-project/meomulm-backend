package com.meomulm.room.model.dto.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    // 예약 ID
    int reservationId;
    // 유저 ID
    int userId;
    // 객실 ID
    int productId;
    // 예약자명
    String bookerName;
    // 예약자 이메일
    String bookerEmail;
    // 예약자 전화번호
    String bookerPhone;
    // 체크인 날짜
    String checkInDate;
    // 체크아웃 날짜
    String checkOutDate;
    // 예약 인원 수
    int guestCount;
    // 예약 상태
    String status;
    // 총 가격
    int totalPrice;
    // 생성일자
    String createdAt;
    // 변경일자
    String updatedAt;


}
