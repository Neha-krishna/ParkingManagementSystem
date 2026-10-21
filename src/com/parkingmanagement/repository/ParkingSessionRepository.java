package com.parkingmanagement.repository;

import com.parkingmanagement.model.ParkingSession;

public interface ParkingSessionRepository {
    boolean startSession(String vehicleNumber, int slotId, String entryType);
    boolean endSession(String vehicleNumber, double fee);
    ParkingSession findActiveSessionByVehicle(String vehicleNumber);
    void displayActiveSessions();
}