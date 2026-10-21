package com.parkingmanagement.model;

public class ParkingSession {
    private int sessionId;
    private final String vehicleNumber;
    private final int slotId;
    private final String entryTime;
    private String exitTime;
    private final String entryType;
    private double fee;
    private String status;
    private Integer reservationId;

    public ParkingSession(String vehicleNumber, int slotId, String entryTime, String entryType) {
        this.vehicleNumber = vehicleNumber;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.entryType = entryType;
        this.status = "ACTIVE";
    }
    public ParkingSession(int sessionId, String vehicleNumber, int slotId, String entryTime, 
                          String exitTime, String entryType, double fee, String status, Integer reservationId) {
        this.sessionId = sessionId;
        this.vehicleNumber = vehicleNumber;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.entryType = entryType;
        this.fee = fee;
        this.status = status;
        this.reservationId = reservationId;
    }

    
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public int getSlotId() { return slotId; }
    public String getEntryTime() { return entryTime; }
    public String getExitTime() { return exitTime; }
    public void setExitTime(String exitTime) { this.exitTime = exitTime; }
    public String getEntryType() { return entryType; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
}