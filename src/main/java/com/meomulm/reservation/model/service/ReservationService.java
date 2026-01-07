package com.meomulm.reservation.model.service;

import com.meomulm.reservation.model.dto.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ReservationService {
    void addReservation(Reservation reservation);
    void updateReservation(Reservation reservation);
    void deleteReservation(int reservationId);
}
