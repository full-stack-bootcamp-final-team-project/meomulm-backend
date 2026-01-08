package com.meomulm.reservation.controller;

import com.meomulm.common.exception.UnauthorizedException;
import com.meomulm.common.util.JwtUtil;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final JwtUtil jwtUtil;

    @GetMapping("/")
    public ResponseEntity<List<Reservation>> getAllReservationsByUserId(
            @RequestHeader("Authorization") String authHeader) {
        String token = verifyToken(authHeader);
        int loginUserId = jwtUtil.getUserIdFromToken(token);
        List<Reservation> reservations = reservationService.getAllReservationsByUserId(loginUserId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<Reservation> getAllReservationById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int reservationId) {
        String token = verifyToken(authHeader);
        int loginUserId = jwtUtil.getUserIdFromToken(token);
        Reservation reservations = reservationService.getReservationById(reservationId, loginUserId);
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/")
    public ResponseEntity<Void> addReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Reservation reservation) {
        String token = verifyToken(authHeader);
        int loginUserId = jwtUtil.getUserIdFromToken(token);
        reservation.setUserId(loginUserId);
        reservationService.addReservation(reservation);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int reservationId,
            @RequestBody Reservation reservation) {
        String token = verifyToken(authHeader);
        int loginUserId = jwtUtil.getUserIdFromToken(token);
        reservation.setReservationId(reservationId);
        reservationService.updateReservation(reservation, loginUserId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int reservationId,
            @RequestBody Reservation reservation) {
        String token = verifyToken(authHeader);
        int loginUserId = jwtUtil.getUserIdFromToken(token);
        reservation.setReservationId(reservationId);
        reservationService.deleteReservation(reservation, loginUserId);
        return ResponseEntity.ok().build();
    }

    /*
    Bearer 토큰 유효성 검사
    예외 처리 또는 사용자 ID 추출
     */
    private String verifyToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("인증 헤더가 없거나 잘못되었습니다.");
        }
        return authHeader.substring(7);
    }
}
