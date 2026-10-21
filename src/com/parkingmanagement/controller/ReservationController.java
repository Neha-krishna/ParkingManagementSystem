package com.parkingmanagement.controller;

import com.parkingmanagement.model.Reservation;
import com.parkingmanagement.service.ReservationService;
import com.parkingmanagement.view.ReservationView;

import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ReservationController {
    private final ReservationView view;
    private final ReservationService service;

    public ReservationController(ReservationView view, ReservationService service) {
        this.view = view;
        this.service = service;

        this.view.setSubmitAction(e -> handleReservation());
        
        
        java.awt.EventQueue.invokeLater(() -> {
            this.view.setVisible(true);
            this.view.toFront();
            this.view.requestFocus();
        });
    }

    private void handleReservation() {
        try {
            String customerName = view.getCustomerName();
            String vehicleNumber = view.getVehicleNumber();
            String vehicleType = view.getVehicleType();
            int slotId = view.getSelectedSlot();

            if (customerName.isEmpty() || vehicleNumber.isEmpty()) {
                view.showPopup("Customer name and vehicle number cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime start = LocalDateTime.parse(view.getStartTimeText(), formatter);
            LocalDateTime end = LocalDateTime.parse(view.getEndTimeText(), formatter);

            if (!start.isBefore(end)) {
                view.showPopup("End time must be after start time.", "Invalid Time", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Reservation reservation = new Reservation(
                    customerName,
                    vehicleNumber,
                    vehicleType,
                    slotId,
                    start.toLocalDate().toString(),
                    start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    end.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
            );

            boolean success = service.bookReservation(reservation);

            if (success) {
                view.showPopup("Reservation successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                view.dispose();
            } else {
                view.showPopup("Reservation failed. The slot is already booked for this time window.", "Booking Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (DateTimeParseException e) {
            view.showPopup("Please enter dates and times in the exact format: yyyy-MM-dd HH:mm", "Invalid Format", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            view.showPopup("An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}