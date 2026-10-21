package com.parkingmanagement.repository;

import com.parkingmanagement.model.Vehicle;
import java.util.List;

public interface VehicleRepository {
    boolean save(Vehicle vehicle);
    List<Vehicle> findAll();
    Vehicle findByNumber(String vehicleNumber);
}
