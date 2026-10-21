package com.parkingmanagement.service;

import com.parkingmanagement.model.ParkingSession;
import com.parkingmanagement.repository.ParkingSessionRepository;
import com.parkingmanagement.repository.ParkingSlotRepository;
import com.parkingmanagement.repository.ReservationRepository;
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingService {
    private final ParkingSlotRepository slotRepo;
    private final ParkingSessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;

    public ParkingService(ParkingSlotRepository slotRepo, ParkingSessionRepository sessionRepo,
                          ReservationRepository reservationRepo) {
        this.slotRepo = slotRepo;
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;
    }

    public boolean parkWalkInVehicle(String vehicleNumber, String vehicleType) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty() || vehicleType == null) return false;
        String number = vehicleNumber.trim().toUpperCase();
        String type = vehicleType.trim().toUpperCase();
        if (!type.equals("CAR") && !type.equals("BIKE")) return false;

        if (sessionRepo.findActiveSessionByVehicle(number) != null) {
            System.out.println("This vehicle already has an active parking session.");
            return false;
        }
        long availableSlots = slotRepo.countAvailableSlots(type);
        long reservedSlots = reservationRepo.countActiveReservationsForToday(type);
        if (availableSlots <= reservedSlots) {
            System.out.println("No unreserved parking capacity available for " + type);
            return false;
        }
        int slotId = slotRepo.findFirstAvailableSlotId(type);
        if (slotId == -1) return false;

        slotRepo.updateStatus(slotId, "OCCUPIED");
        if (!sessionRepo.startSession(number, slotId, "WALK_IN")) {
            slotRepo.updateStatus(slotId, "AVAILABLE");
            return false;
        }
        System.out.println("Vehicle parked successfully in slot S" + String.format("%02d", slotId));
        return true;
    }

  public boolean checkoutVehicle(String vehicleNumber, String vehicleType) {
    return checkoutVehicleAndGetFee(vehicleNumber, vehicleType) >= 0;
}

public double checkoutVehicleAndGetFee(
        String vehicleNumber, String vehicleType) {

    if (vehicleNumber == null || vehicleType == null) {
        return -1;
    }

    String number = vehicleNumber.trim().toUpperCase();

    ParkingSession session =
            sessionRepo.findActiveSessionByVehicle(number);

    if (session == null) {
        System.out.println("No active session found for: " + number);
        return -1;
    }

    try {
        LocalDateTime entryTime = LocalDateTime.parse(
                session.getEntryTime().trim().replace(" ", "T"));

        long minutes = Math.max(
                0,
                Duration.between(entryTime, LocalDateTime.now())
                        .toMinutes());

        long hours = Math.max(1, (minutes + 59) / 60);

        double rate = "CAR".equalsIgnoreCase(vehicleType)
                ? 30.0
                : 15.0;

        double fee = hours * rate;

        if (!sessionRepo.endSession(number, fee)) {
            return -1;
        }

        slotRepo.updateStatus(session.getSlotId(), "AVAILABLE");

        System.out.println("Checkout successful. Fee: ₹" + fee);

        return fee;

    } catch (RuntimeException ex) {
        System.err.println("Checkout failed: " + ex.getMessage());
        return -1;
    }
}
}
