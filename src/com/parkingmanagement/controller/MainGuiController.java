package com.parkingmanagement.controller;

import com.parkingmanagement.config.Database;
import com.parkingmanagement.model.Bike;
import com.parkingmanagement.model.Car;
import com.parkingmanagement.model.Reservation;
import com.parkingmanagement.model.Vehicle;
import com.parkingmanagement.repository.ParkingSessionRepository;
import com.parkingmanagement.repository.ParkingSlotRepository;
import com.parkingmanagement.repository.ReservationRepository;
import com.parkingmanagement.repository.VehicleRepository;
import com.parkingmanagement.service.ParkingService;
import com.parkingmanagement.service.ReservationService;
import com.parkingmanagement.view.MainDashboardView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MainGuiController {
    private final MainDashboardView view;
    private final ParkingService parkingService;
    private final ReservationService reservationService;
    private final ParkingSlotRepository slotRepo;
    private final VehicleRepository vehicleRepo;
    private final ParkingSessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;

    public MainGuiController(MainDashboardView view, ParkingService parkingService,
                             ReservationService reservationService, ParkingSlotRepository slotRepo,
                             VehicleRepository vehicleRepo, ParkingSessionRepository sessionRepo,
                             ReservationRepository reservationRepo) {
        this.view = view;
        this.parkingService = parkingService;
        this.reservationService = reservationService;
        this.slotRepo = slotRepo;
        this.vehicleRepo = vehicleRepo;
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;
        initController();
        view.setVisible(true);
    }

    private void initController() {
        view.getSlotsButton().addActionListener(e -> showParkingSlots());
        view.getVehicleButton().addActionListener(e -> registerVehicle());
        view.getViewVehiclesButton().addActionListener(e -> viewVehicles());
        view.getReservationButton().addActionListener(e -> makeReservation());
        view.getManageReservationsButton().addActionListener(e -> manageReservations());
        view.getEntryButton().addActionListener(e -> vehicleEntry());
        view.getExitButton().addActionListener(e -> vehicleExit());
    }

    private void showParkingSlots() {
        JFrame frame = new JFrame("Parking Slot Status");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(view);
        JPanel panel = new JPanel(new GridLayout(0, 5, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        String sql = "SELECT slotId, slotType, status FROM ParkingSlots ORDER BY slotId";
        try (Connection conn = Database.connect()) {
            if (conn == null) throw new SQLException("Could not connect to the database.");
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("slotId");
                    String type = rs.getString("slotType");
                    String statusText = rs.getString("status");
                    JPanel slot = new JPanel(new GridLayout(3, 1));
                    slot.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    slot.add(new JLabel(String.format("S%02d", id), SwingConstants.CENTER));
                    slot.add(new JLabel(type, SwingConstants.CENTER));
                    slot.add(new JLabel(statusText, SwingConstants.CENTER));
                    if ("AVAILABLE".equalsIgnoreCase(statusText)) slot.setBackground(new Color(190, 240, 200));
                    else if ("OCCUPIED".equalsIgnoreCase(statusText)) slot.setBackground(new Color(250, 190, 190));
                    else slot.setBackground(new Color(255, 225, 160));
                    panel.add(slot);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Unable to load parking slots: " + ex.getMessage());
        }
        frame.add(new JScrollPane(panel));
        frame.setVisible(true);
    }

    private void registerVehicle() {
        JTextField numberField = new JTextField();
        JTextField ownerField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"CAR", "BIKE"});
        Object[] fields = {"Vehicle Registration Number:", numberField, "Owner Name:", ownerField, "Vehicle Type:", typeBox};
        if (JOptionPane.showConfirmDialog(view, fields, "Register Vehicle", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        String number = numberField.getText().trim().toUpperCase();
        String owner = ownerField.getText().trim();
        String type = (String) typeBox.getSelectedItem();
        if (number.isEmpty() || owner.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter all vehicle details.");
            return;
        }
        Vehicle vehicle = "BIKE".equals(type) ? new Bike(number, owner) : new Car(number, owner);
        if (vehicleRepo.save(vehicle)) JOptionPane.showMessageDialog(view, "Vehicle registered successfully!");
        else JOptionPane.showMessageDialog(view, "Registration failed. The number may already be registered or the database may be unavailable.");
    }

    private void viewVehicles() {
        String[] columns = {"Vehicle Number", "Owner Name", "Vehicle Type"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Vehicle vehicle : vehicleRepo.findAll())
            model.addRow(new Object[]{vehicle.getVehicleNumber(), vehicle.getOwnerName(), vehicle.getVehicleType()});
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        JOptionPane.showMessageDialog(view, new JScrollPane(table), "Registered Vehicles", JOptionPane.PLAIN_MESSAGE);
    }

    private void vehicleEntry() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        if (vehicles.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please register a vehicle first.");
            return;
        }
        String[] numbers = vehicles.stream().map(Vehicle::getVehicleNumber).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(view, "Select entering vehicle:", "Vehicle Entry",
                JOptionPane.QUESTION_MESSAGE, null, numbers, numbers[0]);
        if (selected == null) return;
        Vehicle vehicle = vehicleRepo.findByNumber(selected);
        if (vehicle == null) {
            JOptionPane.showMessageDialog(view, "Vehicle not found.");
            return;
        }
        if (parkingService.parkWalkInVehicle(vehicle.getVehicleNumber(), vehicle.getVehicleType()))
            JOptionPane.showMessageDialog(view, "Vehicle entry recorded successfully!");
        else JOptionPane.showMessageDialog(view, "Entry failed. The vehicle may already be parked or no suitable slot is available.");
    }

    private void vehicleExit() {
    List<Vehicle> vehicles = vehicleRepo.findAll();

    if (vehicles.isEmpty()) {
        JOptionPane.showMessageDialog(
                view, "No registered vehicles found.");
        return;
    }

    String[] activeNumbers = vehicles.stream()
            .filter(v -> sessionRepo.findActiveSessionByVehicle(
                    v.getVehicleNumber()) != null)
            .map(Vehicle::getVehicleNumber)
            .toArray(String[]::new);

    if (activeNumbers.length == 0) {
        JOptionPane.showMessageDialog(
                view, "There are no vehicles currently parked.");
        return;
    }

    String selected = (String) JOptionPane.showInputDialog(
            view,
            "Select exiting vehicle:",
            "Vehicle Exit",
            JOptionPane.QUESTION_MESSAGE,
            null,
            activeNumbers,
            activeNumbers[0]);

    if (selected == null) {
        return;
    }

    Vehicle vehicle = vehicleRepo.findByNumber(selected);

    if (vehicle == null) {
        JOptionPane.showMessageDialog(
                view, "Vehicle not found.");
        return;
    }

    double fee = parkingService.checkoutVehicleAndGetFee(
            selected, vehicle.getVehicleType());

    if (fee >= 0) {
        JOptionPane.showMessageDialog(
                view,
                "Vehicle exit recorded successfully!\n\n"
                + "Vehicle Number: " + selected + "\n"
                + "Vehicle Type: " + vehicle.getVehicleType() + "\n"
                + "Parking Fee: ₹" + String.format("%.2f", fee),
                "Checkout Receipt",
                JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(
                view,
                "Exit failed. Please try again.",
                "Checkout Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

    private void makeReservation() {
        JTextField customerField = new JTextField();
        JTextField vehicleField = new JTextField();
        JComboBox<String> slotTypeBox = new JComboBox<>(new String[]{"CAR", "BIKE"});
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField startField = new JTextField("10:00");
        JTextField endField = new JTextField("11:00");
        Object[] fields = {"Customer Name:", customerField, "Registered Vehicle Number:", vehicleField,
                "Slot Type:", slotTypeBox, "Reservation Date (YYYY-MM-DD):", dateField,
                "Start Time (HH:MM):", startField, "End Time (HH:MM):", endField};
        if (JOptionPane.showConfirmDialog(view, fields, "Customer Reservation", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String customer = customerField.getText().trim();
        String number = vehicleField.getText().trim().toUpperCase();
        String type = (String) slotTypeBox.getSelectedItem();
        String date = dateField.getText().trim();
        String start = startField.getText().trim();
        String end = endField.getText().trim();
        if (customer.isEmpty() || number.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter the customer name and vehicle number."); return;
        }
        Vehicle vehicle = vehicleRepo.findByNumber(number);
        if (vehicle == null || !vehicle.getVehicleType().equalsIgnoreCase(type)) {
            JOptionPane.showMessageDialog(view, "Register the vehicle first and choose its correct type."); return;
        }
        try {
            LocalDate.parse(date);
            LocalTime startTime = LocalTime.parse(start);
            LocalTime endTime = LocalTime.parse(end);
            if (!endTime.isAfter(startTime)) throw new IllegalArgumentException("End time must be after start time.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Check the date and time formats. Use YYYY-MM-DD and HH:MM, and make sure end time is later."); return;
        }
        int slotId = findSlotIdForReservation(type, date, start, end);
        if (slotId < 0) {
            JOptionPane.showMessageDialog(view, "No slot of type " + type + " is available for that reservation time."); return;
        }
        Reservation reservation = new Reservation(customer, number, type, slotId, date, start, end);
        if (reservationService.bookReservation(reservation)) JOptionPane.showMessageDialog(view, "Reservation saved successfully! Slot S" + String.format("%02d", slotId));
        else JOptionPane.showMessageDialog(view, "Reservation failed. That slot may already be reserved for this time.");
    }

    private int findSlotIdForReservation(String type, String date, String start, String end) {
        String sql = "SELECT slotId FROM ParkingSlots WHERE slotType=? ORDER BY slotId";
        try (Connection conn = Database.connect()) {
            if (conn == null) return -1;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, type);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int slotId = rs.getInt("slotId");
                        if (reservationRepo.isSlotAvailableForReservation(type, slotId, date, start, end)) return slotId;
                    }
                }
            }
        } catch (SQLException e) { System.err.println("Could not find a reservation slot: " + e.getMessage()); }
        return -1;
    }

    private void manageReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No reservations found."); return;
        }
        String[] options = reservations.stream().map(r -> "ID: " + r.getReservationId() + " | " + r.getCustomerName() +
                " | " + r.getVehicleNumber() + " | " + r.getReservationDate() + " " + r.getStartTime() + " | " + r.getStatus()).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(view, "Select a reservation to cancel:", "Manage Reservations",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (selected == null) return;
        int id;
        try { id = Integer.parseInt(selected.substring(4, selected.indexOf(" | ")).trim()); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(view, "Could not read reservation ID."); return; }
        if (reservationService.cancelReservation(id)) JOptionPane.showMessageDialog(view, "Reservation cancelled.");
        else JOptionPane.showMessageDialog(view, "Reservation could not be cancelled. It may already be completed or cancelled.");
    }
}
