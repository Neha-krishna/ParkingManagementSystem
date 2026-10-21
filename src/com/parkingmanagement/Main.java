package com.parkingmanagement;

import com.parkingmanagement.config.Database;
import com.parkingmanagement.controller.MainGuiController;
import com.parkingmanagement.repository.*;
import com.parkingmanagement.service.ParkingService;
import com.parkingmanagement.service.ReservationService;
import com.parkingmanagement.view.MainDashboardView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Database.createTables();
        ParkingSlotRepository slotRepo = new SQLiteParkingSlotRepository();
        slotRepo.initializeSlots();
        ParkingSessionRepository sessionRepo = new SQLiteParkingSessionRepository();
        ReservationRepository reservationRepo = new SQLiteReservationRepository();
        VehicleRepository vehicleRepo = new SQLiteVehicleRepository();
        ParkingService parkingService = new ParkingService(slotRepo, sessionRepo, reservationRepo);
        ReservationService reservationService = new ReservationService(reservationRepo);
        SwingUtilities.invokeLater(() -> {
            MainDashboardView view = new MainDashboardView();
            new MainGuiController(view, parkingService, reservationService, slotRepo,
                    vehicleRepo, sessionRepo, reservationRepo);
        });
    }
}
