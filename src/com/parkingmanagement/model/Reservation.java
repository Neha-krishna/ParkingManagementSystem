package com.parkingmanagement.model;

public class Reservation {
    private int reservationId;
    private final String customerName;
    private final String vehicleNumber;
    private final String slotType;
    private final int slotId;
    private final String reservationDate;
    private final String startTime;
    private final String endTime;
    private String status;

    public Reservation(String customerName, String vehicleNumber, String slotType,
                       int slotId, String reservationDate, String startTime, String endTime) {
        this.customerName = customerName;
        this.vehicleNumber = vehicleNumber;
        this.slotType = slotType;
        this.slotId = slotId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "BOOKED";
    }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public String getCustomerName() { return customerName; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getSlotType() { return slotType; }
    public int getSlotId() { return slotId; }
    public String getReservationDate() { return reservationDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}