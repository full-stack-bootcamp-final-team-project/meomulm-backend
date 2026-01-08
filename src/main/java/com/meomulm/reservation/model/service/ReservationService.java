package com.meomulm.reservation.model.service;

import com.meomulm.reservation.model.dto.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ReservationService {
    List<Reservation> getAllReservationsByUserId(int loginUserId);
    Reservation getReservationById(int reservationId, int loginUserId);
    void addReservation(Reservation reservation);
    void updateReservation(Reservation reservation, int loginUserId);
    void deleteReservation(Reservation reservation, int loginUserId);
}
