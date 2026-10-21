package com.parkingmanagement.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReservationView extends JFrame {

    private final JTextField vehicleField;
    private final JTextField nameField;
    private final JComboBox<String> typeBox;
    private final JComboBox<Integer> slotBox;
    private final JTextField startField;
    private final JTextField endField;
    private final JButton reserveButton;

    public ReservationView() {
        setTitle("Parking Reservation System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(240, 244, 248));

        Font font = new Font("Arial", Font.PLAIN, 16);

        JLabel heading = new JLabel(
            "Parking Reservation System",
            SwingConstants.CENTER
        );
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        vehicleField = new JTextField(15);
        nameField = new JTextField(15);
        typeBox = new JComboBox<>(new String[]{"CAR", "BIKE"});
        slotBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        startField = new JTextField("2026-10-21 10:00", 15);
        endField = new JTextField("2026-10-21 11:00", 15);

        reserveButton = new JButton("Reserve Slot");

        JComponent[] fields = {
            vehicleField, nameField, typeBox,
            slotBox, startField, endField
        };

        for (JComponent field : fields) {
            field.setFont(font);
        }
        reserveButton.setFont(font);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        form.setOpaque(false);

        form.add(new JLabel("Vehicle Number:"));
        form.add(vehicleField);

        form.add(new JLabel("Customer Name:"));
        form.add(nameField);

        form.add(new JLabel("Vehicle Type:"));
        form.add(typeBox);

        form.add(new JLabel("Parking Slot ID:"));
        form.add(slotBox);

        form.add(new JLabel("Start (yyyy-MM-dd HH:mm):"));
        form.add(startField);

        form.add(new JLabel("End (yyyy-MM-dd HH:mm):"));
        form.add(endField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(reserveButton);

        setLayout(new BorderLayout(10, 10));
        add(heading, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    public String getVehicleNumber() {
        return vehicleField.getText().trim();
    }

    public String getCustomerName() {
        return nameField.getText().trim();
    }

    public String getVehicleType() {
        return (String) typeBox.getSelectedItem();
    }

    public int getSelectedSlot() {
        return (Integer) slotBox.getSelectedItem();
    }

    public String getStartTimeText() {
        return startField.getText().trim();
    }

    public String getEndTimeText() {
        return endField.getText().trim();
    }

    public void setSubmitAction(ActionListener listener) {
        reserveButton.addActionListener(listener);
    }

    public void showPopup(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(
            this, message, title, messageType
        );
    }
}