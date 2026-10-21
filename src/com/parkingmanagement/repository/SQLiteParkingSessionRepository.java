package com.parkingmanagement.repository;

import com.parkingmanagement.config.Database;
import java.sql.*;
import java.time.LocalDateTime;

public class SQLiteParkingSessionRepository implements ParkingSessionRepository {

    @Override
    public boolean startSession(String vehicleNumber, int slotId, String entryType) {
        String sql = "INSERT INTO ParkingSessions (vehicleNumber, slotId, entryTime, entryType, status) VALUES (?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = Database.connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleNumber);
            pstmt.setInt(2, slotId);
            pstmt.setString(3, LocalDateTime.now().toString());
            pstmt.setString(4, entryType);
            return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error starting session: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean endSession(String vehicleNumber, double fee) {
        String sql = "UPDATE ParkingSessions SET exitTime = ?, fee = ?, status = 'COMPLETED' WHERE vehicleNumber = ? AND status = 'ACTIVE'";
        try (Connection conn = Database.connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDateTime.now().toString());
            pstmt.setDouble(2, fee);
            pstmt.setString(3, vehicleNumber);
            return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error ending session: " + e.getMessage());
            return false;
        }
    }


    @Override
    public com.parkingmanagement.model.ParkingSession findActiveSessionByVehicle(String vehicleNumber) {
        String sql = "SELECT sessionId, vehicleNumber, slotId, entryTime, exitTime, entryType, fee, status, reservationId " +
                     "FROM ParkingSessions WHERE UPPER(vehicleNumber)=UPPER(?) AND status='ACTIVE' ORDER BY sessionId DESC LIMIT 1";
        try (Connection conn = Database.connect()) {
            if (conn == null) return null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, vehicleNumber.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int reservationValue = rs.getInt("reservationId");
                        Integer reservationId = rs.wasNull() ? null : reservationValue;
                        return new com.parkingmanagement.model.ParkingSession(
                            rs.getInt("sessionId"), rs.getString("vehicleNumber"), rs.getInt("slotId"),
                            rs.getString("entryTime"), rs.getString("exitTime"), rs.getString("entryType"),
                            rs.getDouble("fee"), rs.getString("status"), reservationId);
                    }
                }
            }
        } catch (SQLException e) { System.err.println("Unable to find active session: " + e.getMessage()); }
        return null;
    }

    @Override
    public void displayActiveSessions() {
        String sql = """
            SELECT ps.vehicleNumber, ps.slotId, ps.entryTime, ps.entryType, v.vehicleType 
            FROM ParkingSessions ps
            JOIN Vehicles v ON UPPER(ps.vehicleNumber) = UPPER(v.vehicleNumber)
            WHERE ps.status = 'ACTIVE'
        """;
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Active Parking Sessions ---");
            while (rs.next()) {
                System.out.printf("Vehicle: %s | Type: %s | Slot: S%02d | Entry: %s\n",
                        rs.getString("vehicleNumber"), rs.getString("vehicleType"),
                        rs.getInt("slotId"), rs.getString("entryTime"));
            }
        } catch (SQLException e) {
            System.out.println("Error displaying active sessions: " + e.getMessage());
        }
    }
}