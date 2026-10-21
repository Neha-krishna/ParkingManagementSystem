package com.parkingmanagement.repository;

import java.util.List;

public interface ParkingSlotRepository {
    void addSlot(String slotType);
    void updateStatus(int slotId, String status);
    long countAvailableSlots(String slotType);
    int findFirstAvailableSlotId(String slotType);
    void initializeSlots();
    void displaySlots();
}