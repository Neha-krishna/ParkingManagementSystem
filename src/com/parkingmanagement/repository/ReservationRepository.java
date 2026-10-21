package com.parkingmanagement.repository;

import com.parkingmanagement.model.Reservation;
import java.util.List;

public interface ReservationRepository {
    boolean save(Reservation reservation);
    List<Reservation> findAll();
    boolean cancel(int reservationId);
    boolean isSlotAvailableForReservation(String slotType, int slotId, String date, String start, String end);
    long countActiveReservationsForToday(String slotType);
}