package com.meomulm.reservation.controller;

import com.meomulm.common.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @PutMapping("/")
    public ResponseEntity<Void> addReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Reservation reservation) {
        String token = authHeader.substring(7);
        int loginUserId = jwtUtil.getUserIdFromToken(token);
        reservation.setUserId(loginUserId);
        reservationService.addReservation(reservation);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int reservationId,
            @RequestBody Reservation reservation) {
        String token = authHeader.substring(7);
        int loginUserId = jwtUtil.getUserIdFromToken(token);
        reservation.setReservationId(reservationId);
        reservation.setUserId(loginUserId);
        reservationService.updateReservation(reservation);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int reservationId) {
        String token = authHeader.substring(7);
        int loginUserId = jwtUtil.getUserIdFromToken(token);
        reservationService.deleteReservation(reservationId, loginUserId);
        return ResponseEntity.ok().build();
    }
}
