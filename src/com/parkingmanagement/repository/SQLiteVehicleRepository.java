package com.parkingmanagement.repository;

import com.parkingmanagement.config.Database;
import com.parkingmanagement.model.Bike;
import com.parkingmanagement.model.Car;
import com.parkingmanagement.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteVehicleRepository implements VehicleRepository {
    @Override
    public boolean save(Vehicle vehicle) {
        String sql = "INSERT INTO Vehicles(vehicleNumber, ownerName, vehicleType) VALUES(?,?,?)";
        try (Connection conn = Database.connect()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, vehicle.getVehicleNumber().trim().toUpperCase());
                ps.setString(2, vehicle.getOwnerName().trim());
                ps.setString(3, vehicle.getVehicleType().toUpperCase());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Unable to register vehicle: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT vehicleNumber, ownerName, vehicleType FROM Vehicles ORDER BY vehicleNumber";
        try (Connection conn = Database.connect()) {
            if (conn == null) return vehicles;
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) vehicles.add(toVehicle(rs.getString("vehicleNumber"), rs.getString("ownerName"), rs.getString("vehicleType")));
            }
        } catch (SQLException e) { System.err.println("Unable to load vehicles: " + e.getMessage()); }
        return vehicles;
    }

    @Override
    public Vehicle findByNumber(String vehicleNumber) {
        String sql = "SELECT vehicleNumber, ownerName, vehicleType FROM Vehicles WHERE UPPER(vehicleNumber)=UPPER(?)";
        try (Connection conn = Database.connect()) {
            if (conn == null) return null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, vehicleNumber.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return toVehicle(rs.getString("vehicleNumber"), rs.getString("ownerName"), rs.getString("vehicleType"));
                }
            }
        } catch (SQLException e) { System.err.println("Unable to find vehicle: " + e.getMessage()); }
        return null;
    }

    private Vehicle toVehicle(String number, String owner, String type) {
        return "BIKE".equalsIgnoreCase(type) ? new Bike(number, owner) : new Car(number, owner);
    }
}
