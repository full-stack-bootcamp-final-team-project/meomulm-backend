package com.meomulm.reservation.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final AuthUtil authUtil;

    /**
     * 예약 추가
     * @param authHeader JWT 토큰
     * @param reservation 예약 DTO
     * @return 상태코드 200
     */
    @PostMapping
    public ResponseEntity<Void> postReservation(@RequestHeader("Authorization") String authHeader, @RequestBody Reservation reservation) {
        int loginUserId = authUtil.getCurrentUserId(authHeader);
        reservation.setUserId(loginUserId);
        reservationService.postReservation(reservation);
        return ResponseEntity.ok().build();
    }

    /**
     * 예약 수정
     * @param authHeader JWT 토큰
     * @param reservation 예약 DTO
     * @return 상태코드 200
     */
    @PatchMapping
    public ResponseEntity<Void> patchReservation(@RequestHeader("Authorization") String authHeader, @RequestBody Reservation reservation) {
        int loginUserId = authUtil.getCurrentUserId(authHeader);
        reservationService.patchReservation(reservation, loginUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * 예약 취소 (상태만 변경)
     * @param authHeader JWT 토큰
     * @param reservation 예약 DTO
     * @return 상태코드 200
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteReservation(@RequestHeader("Authorization") String authHeader, @RequestBody Reservation reservation) {
        int loginUserId = authUtil.getCurrentUserId(authHeader);
        reservationService.deleteReservation(reservation, loginUserId);
        return ResponseEntity.ok().build();
    }
}