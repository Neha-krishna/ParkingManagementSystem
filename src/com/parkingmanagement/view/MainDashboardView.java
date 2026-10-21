package com.parkingmanagement.view;

import javax.swing.*;
import java.awt.*;

public class MainDashboardView extends JFrame {
    private final JButton slotsButton = new JButton("View Parking Slots");
    private final JButton vehicleButton = new JButton("Register Vehicle");
    private final JButton viewVehiclesButton = new JButton("View Vehicles");
    private final JButton reservationButton = new JButton("Make Reservation");
    private final JButton manageReservationsButton = new JButton("Manage Reservations");
    private final JButton entryButton = new JButton("Vehicle Entry");
    private final JButton exitButton = new JButton("Vehicle Exit");

    public MainDashboardView() {
        super("Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 430);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        JLabel title = new JLabel("PARKING MANAGEMENT SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        content.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 2, 12, 12));
        for (JButton button : new JButton[]{slotsButton, vehicleButton, viewVehiclesButton,
                reservationButton, manageReservationsButton, entryButton, exitButton}) {
            button.setFocusPainted(false);
            buttons.add(button);
        }
        content.add(buttons, BorderLayout.CENTER);
        JLabel footer = new JLabel("Manage vehicles, parking sessions and reservations", SwingConstants.CENTER);
        content.add(footer, BorderLayout.SOUTH);
        setContentPane(content);
    }

    public JButton getSlotsButton() { return slotsButton; }
    public JButton getVehicleButton() { return vehicleButton; }
    public JButton getViewVehiclesButton() { return viewVehiclesButton; }
    public JButton getReservationButton() { return reservationButton; }
    public JButton getManageReservationsButton() { return manageReservationsButton; }
    public JButton getEntryButton() { return entryButton; }
    public JButton getExitButton() { return exitButton; }
}
