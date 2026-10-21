package com.parkingmanagement.service;

import com.parkingmanagement.model.Reservation;
import com.parkingmanagement.repository.ReservationRepository;
import java.util.List;

public class ReservationService {
    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public boolean bookReservation(Reservation reservation) {
        if (!repository.isSlotAvailableForReservation(
                reservation.getSlotType(), reservation.getSlotId(),
                reservation.getReservationDate(), reservation.getStartTime(), reservation.getEndTime())) {
            System.out.println("Slot is unavailable or already booked for this time window.");
            return false;
        }
        return repository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    public boolean cancelReservation(int id) {
        return repository.cancel(id);
    }
}