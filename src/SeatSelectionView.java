import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionView {

    public static void showSeatMap(JFrame frame, int showtimeId, Runnable backToMenuCallback, Theatre theatre, Movie movie, Showtime showtime, RegisteredUser loggedInUser) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Fetch seats for the selected showtime
        List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
        List<Seat> selectedSeats = new ArrayList<>();

        int columns = (int) Math.ceil(Math.sqrt(seats.size()));
        int rows = (int) Math.ceil((double) seats.size() / columns);

        JPanel seatsPanel = new JPanel(new GridLayout(rows, columns, 5, 5));
        seatsPanel.setOpaque(false);

        JLabel totalCostLabel = new JLabel("Total Cost: $0.00", SwingConstants.CENTER);
        totalCostLabel.setFont(new Font("Courier New", Font.BOLD, 16));
        totalCostLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        double moviePrice = movie.getPrice();

        // Load and resize images
        ImageIcon availableIcon = resizeImage("images/seat.png", 70, 70);
        ImageIcon selectedIcon = resizeImage("images/sel_seat.png", 70, 70);
        ImageIcon bookedIcon = resizeImage("images/booked_seat.png", 70, 70);

        // Create buttons for seats
        for (Seat seat : seats) {
            JButton seatButton = new JButton();
            seatButton.setPreferredSize(new Dimension(70, 70));
            seatButton.setLayout(new BorderLayout()); // Allow text overlay

            // Determine the icon and background color based on seat status
            if ("Available".equalsIgnoreCase(seat.getStatus())) {
                seatButton.setIcon(availableIcon); // Available seat icon
                seatButton.setBackground(Color.WHITE); // Default background for available seats
                seatButton.setContentAreaFilled(false); // Transparent background if an icon is used
            } else {
                seatButton.setIcon(bookedIcon); // Booked seat icon
                seatButton.setDisabledIcon(bookedIcon); // Prevent grayed-out effect
                seatButton.setEnabled(false); // Disable button for booked seats
                seatButton.setBackground(Color.GRAY); // Grey background for booked seats
                seatButton.setContentAreaFilled(true); // Ensure the background color is visible
            }

            // Add seat number as an overlay label
            JLabel seatLabel = new JLabel(seat.getSeatNumber(), SwingConstants.CENTER);
            seatLabel.setForeground(Color.BLACK); // Black text for visibility
            seatLabel.setFont(new Font("Courier New", Font.BOLD, 14));
            seatLabel.setOpaque(false); // Transparent background
            seatButton.add(seatLabel, BorderLayout.CENTER);

            // Action listener for seat selection
            seatButton.addActionListener(e -> {
                if (selectedSeats.contains(seat)) {
                    selectedSeats.remove(seat);
                    seatButton.setIcon(availableIcon); // Deselect seat
                } else {
                    selectedSeats.add(seat);
                    seatButton.setIcon(selectedIcon); // Mark as selected
                }

                // Update total cost
                double totalCost = selectedSeats.size() * moviePrice;
                totalCostLabel.setText(String.format("Total Cost: $%.2f", totalCost));
            });

            seatsPanel.add(seatButton);
        }

        // Create a wrapper panel to center the seat map
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(seatsPanel);

        // Back button
        JButton backButton = VisualGui.createStyledButtonSmall("Back to Showtime Menu");
        backButton.addActionListener(e -> backToMenuCallback.run());

        // Proceed to Payment button
        JButton proceedButton = VisualGui.createStyledButtonSmall("Proceed to Payment");
        proceedButton.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select at least one seat to proceed.");
            } else {
                if (loggedInUser != null) {
                    // If logged in as a registered user
                    PaymentViewRU.showPaymentViewRU(frame, loggedInUser, theatre, movie, showtime, selectedSeats, backToMenuCallback);
                } else {
                    // If not logged in
                    PaymentViewGuest.showPaymentView(frame, theatre, movie, showtime, selectedSeats, backToMenuCallback);
                }
            }
        });

        // Add buttons to a control panel
        JPanel controlPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        controlPanel.add(backButton);
        controlPanel.add(proceedButton);

        // Add components to the frame
        frame.add(totalCostLabel, BorderLayout.NORTH);
        frame.add(centerWrapper, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
        VisualGui.applyGlobalFont(frame.getContentPane(), VisualGui.fontTable);
    }


    // Helper method to resize images
    private static ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(path);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }



    public static void showSeats(JFrame frame, int showtimeId, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
        String[] columnNames = {"Seat ID", "Seat Number", "Status"};

        Object[][] data = new Object[seats.size()][columnNames.length];
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            data[i][0] = seat.getSeatID();
            data[i][1] = seat.getSeatNumber();
            data[i][2] = seat.getStatus();
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(table);

        JButton backButton = new JButton("Back to Showtime Menu");
//        backButton.addActionListener(e -> ShowtimeView.showShowtime(frame));
        backButton.addActionListener(e -> ShowtimeView.showShowtime(frame, () -> GuestView.openGuestMenu(frame)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public static void addSeat(JFrame frame, int showtimeId) {
        JTextField seatNumberField = new JTextField();
        JComboBox<String> statusField = new JComboBox<>(new String[]{"Available", "Reserved"});

        Object[] message = {
                "Seat Number:", seatNumberField,
                "Status:", statusField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add New Seat", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String seatNumber = seatNumberField.getText();
                String status = (String) statusField.getSelectedItem();

                Seat newSeat = new Seat(showtimeId, seatNumber, status);
                if (newSeat.addSeat()) {
                    JOptionPane.showMessageDialog(frame, "Seat added successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to add seat.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please try again.");
            }
        }
    }

    public static void modifySeat(JFrame frame, int showtimeId) {
        List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
        String[] seatNumbers = seats.stream().map(Seat::getSeatNumber).toArray(String[]::new);

        String selectedSeatNumber = (String) JOptionPane.showInputDialog(
                frame,
                "Select a seat to modify:",
                "Modify Seat",
                JOptionPane.PLAIN_MESSAGE,
                null,
                seatNumbers,
                null
        );

        if (selectedSeatNumber != null) {
            Seat seatToEdit = seats.stream()
                    .filter(s -> s.getSeatNumber().equals(selectedSeatNumber))
                    .findFirst()
                    .orElse(null);

            if (seatToEdit != null) {
                JTextField seatNumberField = new JTextField(seatToEdit.getSeatNumber());
                JComboBox<String> statusField = new JComboBox<>(new String[]{"Available", "Reserved"});
                statusField.setSelectedItem(seatToEdit.getStatus());

                Object[] message = {
                        "Seat Number:", seatNumberField,
                        "Status:", statusField
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "Modify Seat", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String seatNumber = seatNumberField.getText();
                        String status = (String) statusField.getSelectedItem();

                        seatToEdit = new Seat((Integer) seatToEdit.getSeatID(), showtimeId, seatNumber, status);
                        if (seatToEdit.modifySeat()) {
                            JOptionPane.showMessageDialog(frame, "Seat updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Failed to update seat.");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, "Invalid input. Please try again.");
                    }
                }
            }
        }
    }


    public static void deleteSeat(JFrame frame, int showtimeId) {
        List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
        String[] seatNumbers = seats.stream().map(Seat::getSeatNumber).toArray(String[]::new);

        String selectedSeatNumber = (String) JOptionPane.showInputDialog(
                frame,
                "Select a seat to delete:",
                "Delete Seat",
                JOptionPane.PLAIN_MESSAGE,
                null,
                seatNumbers,
                null
        );

        if (selectedSeatNumber != null) {
            int confirmation = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to delete seat " + selectedSeatNumber + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                Seat seatToDelete = seats.stream()
                        .filter(s -> s.getSeatNumber().equals(selectedSeatNumber))
                        .findFirst()
                        .orElse(null);

                if (seatToDelete != null && seatToDelete.deleteSeat()) {
                    JOptionPane.showMessageDialog(frame, "Seat deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete seat.");
                }
            }
        }
    }
}