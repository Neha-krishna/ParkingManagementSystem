package com.parkingmanagement.repository;

import com.parkingmanagement.config.Database;
import java.sql.*;

public class SQLiteParkingSlotRepository implements ParkingSlotRepository {

    @Override
    public void addSlot(String slotType) {
        String sql = "INSERT INTO ParkingSlots (slotType) VALUES (?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, slotType);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding slot: " + e.getMessage());
        }
    }

    @Override
    public void updateStatus(int slotId, String status) {
        String sql = "UPDATE ParkingSlots SET status = ? WHERE slotId = ?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, slotId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating slot status: " + e.getMessage());
        }
    }

    @Override
    public long countAvailableSlots(String slotType) {
        String sql = "SELECT COUNT(*) FROM ParkingSlots WHERE slotType = ? AND status = 'AVAILABLE'";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, slotType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting available slots: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int findFirstAvailableSlotId(String slotType) {
        String sql = "SELECT slotId FROM ParkingSlots WHERE slotType = ? AND status = 'AVAILABLE' ORDER BY slotId LIMIT 1";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, slotType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("slotId");
            }
        } catch (SQLException e) {
            System.out.println("Error finding slot: " + e.getMessage());
        }
        return -1;
    }

@Override
    public void initializeSlots() {
        String countSql = "SELECT COUNT(*) FROM ParkingSlots";
        String insertSql = "INSERT INTO ParkingSlots (slotType) VALUES (?)";

        
        try (Connection conn = Database.connect()) {
            if (conn == null) return;
            
            
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(countSql)) {
                
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        for (int i = 1; i <= 10; i++) {
                            String type = (i <= 2 || (i >= 5 && i <= 8)) ? "CAR" : "BIKE";
                            pstmt.setString(1, type);
                            pstmt.executeUpdate();
                        }
                    }
                    conn.commit();
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error initializing slots: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Database connection error during slot initialization: " + e.getMessage());
        }
    }
    @Override
    public void displaySlots() {
        String sql = "SELECT * FROM ParkingSlots ORDER BY slotId";
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Parking Slots Status ---");
            while (rs.next()) {
                System.out.printf("Slot S%02d | Type: %-5s | Status: %s\n",
                        rs.getInt("slotId"), rs.getString("slotType"), rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Error displaying slots: " + e.getMessage());
        }
    }
}