package com.parkingmanagement.repository;

import com.parkingmanagement.model.Reservation;
import com.parkingmanagement.config.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteReservationRepository implements ReservationRepository {

    @Override
    public boolean save(Reservation reservation) {
        String sql = """
            INSERT INTO Reservations (customerName, vehicleNumber, slotType, slotId, reservationDate, startTime, endTime, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'BOOKED')
        """;
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getCustomerName());
            pstmt.setString(2, reservation.getVehicleNumber());
            pstmt.setString(3, reservation.getSlotType());
            pstmt.setInt(4, reservation.getSlotId());
            pstmt.setString(5, reservation.getReservationDate());
            pstmt.setString(6, reservation.getStartTime());
            pstmt.setString(7, reservation.getEndTime());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error saving reservation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM Reservations ORDER BY reservationId";
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservation r = new Reservation(
                        rs.getString("customerName"),
                        rs.getString("vehicleNumber"),
                        rs.getString("slotType"),
                        rs.getInt("slotId"),
                        rs.getString("reservationDate"),
                        rs.getString("startTime"),
                        rs.getString("endTime")
                );
                r.setReservationId(rs.getInt("reservationId"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Error loading reservations: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean cancel(int reservationId) {
        String sql = "UPDATE Reservations SET status = 'CANCELLED' WHERE reservationId = ? AND status = 'BOOKED'";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error cancelling reservation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isSlotAvailableForReservation(String slotType, int slotId, String date, String start, String end) {
        String slotCheck = "SELECT COUNT(*) FROM ParkingSlots WHERE slotId = ? AND slotType = ?";
        String overlapCheck = """
            SELECT COUNT(*) FROM Reservations
            WHERE slotId = ? AND reservationDate = ? AND status = 'BOOKED'
              AND startTime < ? AND endTime > ?
        """;

        try (Connection conn = Database.connect()) {
            try (PreparedStatement pstmt = conn.prepareStatement(slotCheck)) {
                pstmt.setInt(1, slotId);
                pstmt.setString(2, slotType);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) == 0) return false;
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(overlapCheck)) {
                pstmt.setInt(1, slotId);
                pstmt.setString(2, date);
                pstmt.setString(3, end);
                pstmt.setString(4, start);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next() && rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Availability check failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public long countActiveReservationsForToday(String slotType) {
        String sql = """
            SELECT COUNT(*) FROM Reservations
            WHERE slotType = ? AND reservationDate = date('now', 'localtime')
              AND status = 'BOOKED' AND endTime > time('now', 'localtime')
        """;
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, slotType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting active reservations: " + e.getMessage());
        }
        return 0;
    }
}