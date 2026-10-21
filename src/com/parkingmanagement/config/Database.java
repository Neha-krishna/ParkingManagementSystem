package com.parkingmanagement.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:parkingmanagement.db";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL);
           
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            return conn;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }

    public static void createTables() {
        String vehiclesTable = """
            CREATE TABLE IF NOT EXISTS Vehicles (
                vehicleNumber TEXT PRIMARY KEY,
                ownerName TEXT NOT NULL,
                vehicleType TEXT NOT NULL
            );
        """;

        String slotsTable = """
            CREATE TABLE IF NOT EXISTS ParkingSlots (
                slotId INTEGER PRIMARY KEY AUTOINCREMENT,
                slotType TEXT NOT NULL,
                status TEXT DEFAULT 'AVAILABLE'
            );
        """;

        String sessionsTable = """
            CREATE TABLE IF NOT EXISTS ParkingSessions (
                sessionId INTEGER PRIMARY KEY AUTOINCREMENT,
                vehicleNumber TEXT NOT NULL,
                slotId INTEGER NOT NULL,
                entryTime TEXT NOT NULL,
                exitTime TEXT,
                entryType TEXT NOT NULL,
                fee REAL,
                status TEXT DEFAULT 'ACTIVE',
                reservationId INTEGER,
                FOREIGN KEY (slotId) REFERENCES ParkingSlots(slotId)
            );
        """;

        String reservationsTable = """
            CREATE TABLE IF NOT EXISTS Reservations (
                reservationId INTEGER PRIMARY KEY AUTOINCREMENT,
                customerName TEXT NOT NULL,
                vehicleNumber TEXT NOT NULL,
                slotType TEXT NOT NULL,
                slotId INTEGER NOT NULL,
                reservationDate TEXT NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL,
                status TEXT DEFAULT 'BOOKED',
                FOREIGN KEY (slotId) REFERENCES ParkingSlots(slotId)
            );
        """;

        Connection connection = connect();
        if (connection == null) {
            System.err.println("Cannot create database tables because the connection failed.");
            return;
        }
        try (Connection conn = connection; Statement stmt = conn.createStatement()) {
            stmt.execute(vehiclesTable);
            ensureColumn(conn, "Vehicles", "ownerName", "TEXT NOT NULL DEFAULT ''");
            ensureColumn(conn, "Vehicles", "vehicleType", "TEXT NOT NULL DEFAULT 'CAR'");
            stmt.execute(slotsTable);
            stmt.execute(sessionsTable);
            stmt.execute(reservationsTable);
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    private static void ensureColumn(Connection conn, String table, String column, String definition)
            throws SQLException {
        boolean exists = false;
        try (Statement check = conn.createStatement();
             java.sql.ResultSet rs = check.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) {
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            try (Statement alter = conn.createStatement()) {
                alter.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
            }
        }
    }
}