import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionView {

//    public static void showSeatMap(JFrame frame, int showtimeId, Runnable backToMenuCallback) {
//        frame.getContentPane().removeAll();
//        frame.setLayout(new BorderLayout());
//
//        // Fetch seats for the selected showtime
//        List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
//
//        // Panel for displaying seat buttons
//        JPanel seatsPanel = new JPanel(new GridLayout(5, 10, 5, 5)); // Adjust rows/columns as needed
//
//        for (Seat seat : seats) {
//            JButton seatButton = new JButton(seat.getSeatNumber());
//            seatButton.setFont(new Font("Arial", Font.BOLD, 12));
//
//            // Style the button based on the seat status
//            if ("Available".equalsIgnoreCase(seat.getStatus())) {
//                seatButton.setBackground(new Color(144, 238, 144)); // Green for available seats
//            } else {
//                seatButton.setBackground(new Color(255, 99, 71)); // Red for booked seats
//                seatButton.setEnabled(false); // Disable button for booked seats
//            }
//
//            // Action listener for booking a seat
//            seatButton.addActionListener(e -> {
//                int confirm = JOptionPane.showConfirmDialog(
//                        frame,
//                        "Do you want to book seat " + seat.getSeatNumber() + "?",
//                        "Confirm Booking",
//                        JOptionPane.YES_NO_OPTION
//                );
//
//                if (confirm == JOptionPane.YES_OPTION) {
//                    if (seat.reserveSeat(seat.getSeatId())) {
//                        JOptionPane.showMessageDialog(frame, "Seat " + seat.getSeatNumber() + " booked successfully!");
//                        showSeats(frame, showtimeId, backToMenuCallback); // Refresh seat map
//                    } else {
//                        JOptionPane.showMessageDialog(frame, "Failed to book the seat. Please try again.");
//                    }
//                }
//            });
//
//            seatsPanel.add(seatButton);
//        }
//
//        // Back button
//        JButton backButton = new JButton("Back to Showtime Menu");
//        backButton.addActionListener(e -> backToMenuCallback.run());
//
//        frame.add(new JScrollPane(seatsPanel), BorderLayout.CENTER);
//        frame.add(backButton, BorderLayout.SOUTH);
//
//        frame.revalidate();
//        frame.repaint();
//    }
//public static void showSeats(JFrame frame, int showtimeId, Runnable backToMenuCallback) {
//    frame.getContentPane().removeAll();
//    frame.setLayout(new BorderLayout());
//
//    // Fetch seats for the selected showtime
//    List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
//
//    // Panel for displaying seat buttons
//    JPanel seatsPanel = new JPanel(new GridLayout(5, 10, 5, 5)); // Adjust rows/columns as needed
//
//    for (Seat seat : seats) {
//        JButton seatButton = new JButton(seat.getSeatNumber());
//        seatButton.setFont(new Font("Arial", Font.BOLD, 12));
//
//        // Style the button based on the seat status
//        if ("Available".equalsIgnoreCase(seat.getStatus())) {
//            seatButton.setBackground(new Color(144, 238, 144)); // Green for available seats
//        } else {
//            seatButton.setBackground(new Color(255, 99, 71)); // Red for booked seats
//            seatButton.setEnabled(false); // Disable button for booked seats
//        }
//
//        // Action listener for booking a seat
//        seatButton.addActionListener(e -> {
//            int confirm = JOptionPane.showConfirmDialog(
//                    frame,
//                    "Do you want to book seat " + seat.getSeatNumber() + "?",
//                    "Confirm Booking",
//                    JOptionPane.YES_NO_OPTION
//            );
//
//            if (confirm == JOptionPane.YES_OPTION) {
//                if (seat.reserveSeat(seat.getSeatId())) {
//                    JOptionPane.showMessageDialog(frame, "Seat " + seat.getSeatNumber() + " booked successfully!");
//                    showSeats(frame, showtimeId, backToMenuCallback); // Refresh seat map
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Failed to book the seat. Please try again.");
//                }
//            }
//        });
//
//        seatsPanel.add(seatButton);
//    }
//
//    // Back button
//    JButton backButton = new JButton("Back to Showtime Menu");
//    backButton.addActionListener(e -> backToMenuCallback.run());
//
//    frame.add(new JScrollPane(seatsPanel), BorderLayout.CENTER);
//    frame.add(backButton, BorderLayout.SOUTH);
//
//    frame.revalidate();
//    frame.repaint();
//}

    //CODE THAT WORKS
//    public static void showSeatMap(JFrame frame, int showtimeId, Runnable backToMenuCallback) {
//        frame.getContentPane().removeAll();
//        frame.setLayout(new BorderLayout());
//
//        // Fetch seats for the selected showtime
//        List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
//        int totalSeats = seats.size();
//
//        // Calculate rows and columns dynamically
//        int columns = (int) Math.ceil(Math.sqrt(totalSeats)); // Use square root for a balanced layout
//        int rows = (int) Math.ceil((double) totalSeats / columns); // Calculate rows based on total seats
//
//        // Ensure the grid fits all seats
//        if (rows * columns < totalSeats) {
//            columns++; // Adjust columns to fit all seats if necessary
//        }
//
//        // Panel for displaying seat buttons (center layout)
//        JPanel seatsPanel = new JPanel(new GridLayout(rows, columns, 5, 5)); // Dynamic grid with spacing
//        seatsPanel.setOpaque(false); // Ensure background matches parent layout
//
//        for (Seat seat : seats) {
//            JButton seatButton = new JButton(seat.getSeatNumber());
//            seatButton.setFont(new Font("Arial", Font.BOLD, 12)); // Smaller font for compact size
//            seatButton.setPreferredSize(new Dimension(75, 75)); // Adjust button size
//
//            // Style the button based on the seat status
//            if ("Available".equalsIgnoreCase(seat.getStatus())) {
//                seatButton.setBackground(new Color(144, 238, 144)); // Green for available seats
//            } else {
//                seatButton.setBackground(new Color(255, 99, 71)); // Red for booked seats
//                seatButton.setEnabled(false); // Disable button for booked seats
//            }
//
//            // Action listener for booking a seat
//            seatButton.addActionListener(e -> {
//                int confirm = JOptionPane.showConfirmDialog(
//                        frame,
//                        "Do you want to book seat " + seat.getSeatNumber() + "?",
//                        "Confirm Booking",
//                        JOptionPane.YES_NO_OPTION
//                );
//
//                if (confirm == JOptionPane.YES_OPTION) {
//                    if (seat.reserveSeat(seat.getSeatId())) {
//                        JOptionPane.showMessageDialog(frame, "Seat " + seat.getSeatNumber() + " booked successfully!");
//                        showSeatMap(frame, showtimeId, backToMenuCallback); // Refresh seat map
//                    } else {
//                        JOptionPane.showMessageDialog(frame, "Failed to book the seat. Please try again.");
//                    }
//                }
//            });
//
//            seatsPanel.add(seatButton);
//        }
//
//        // Create a wrapper panel to center the seat map
//        JPanel centerWrapper = new JPanel(new GridBagLayout());
//        centerWrapper.setOpaque(false); // Make the wrapper transparent
//        centerWrapper.add(seatsPanel); // Add the seats panel to the wrapper
//
//        // Back button
//        JButton backButton = new JButton("Back to Showtime Menu");
//        backButton.addActionListener(e -> backToMenuCallback.run());
//
//        // Add components to the frame
//        frame.add(centerWrapper, BorderLayout.CENTER); // Center the seat map
//        frame.add(backButton, BorderLayout.SOUTH); // Add the back button to the bottom
//
//        frame.revalidate();
//        frame.repaint();
//    }


    public static void showSeatMap(JFrame frame, int showtimeId, Runnable backToMenuCallback, Theatre theatre, Movie movie, Showtime showtime) {
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
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalCostLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        double moviePrice = movie.getPrice();

        // Create buttons for seats
        for (Seat seat : seats) {
            JButton seatButton = new JButton(seat.getSeatNumber());
            seatButton.setFont(new Font("Arial", Font.BOLD, 12));
            seatButton.setPreferredSize(new Dimension(70, 70));


            if ("Available".equalsIgnoreCase(seat.getStatus())) {
                seatButton.setBackground(new Color(144, 238, 144)); // Green for available seats
            } else {
                seatButton.setBackground(new Color(255, 99, 71)); // Red for booked seats
                seatButton.setEnabled(false); // Disable button for booked seats
            }

            seatButton.addActionListener(e -> {
                if (selectedSeats.contains(seat)) {
                    selectedSeats.remove(seat);
                    seatButton.setBackground(new Color(144, 238, 144)); // Deselect the seat (back to green)
                } else {
                    selectedSeats.add(seat);
                    seatButton.setBackground(Color.YELLOW); // Mark the seat as selected
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
        JButton backButton = new JButton("Back to Showtime Menu");
        backButton.addActionListener(e -> backToMenuCallback.run());

        // Proceed to Payment button
        JButton proceedButton = new JButton("Proceed to Payment");
        proceedButton.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select at least one seat to proceed.");
            } else {
                PaymentView.showPaymentView(frame, theatre, movie, showtime, selectedSeats, backToMenuCallback);
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

//    public static void showSeatMap(JFrame frame, int showtimeId, User user) {
//        frame.getContentPane().removeAll();
//        frame.setLayout(new BorderLayout());
//
//        // Fetch the list of seats for this showtime
//        List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
//
//        // Panel to display the heading
//        JPanel headingPanel = new JPanel();
//        JLabel headingLabel = new JLabel("Select Seats", SwingConstants.CENTER);
//        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        headingLabel.setForeground(new Color(3, 51, 102)); // Dark blue color for the heading
//        headingPanel.add(headingLabel);
//        frame.add(headingPanel, BorderLayout.NORTH);
//
//        // Panel to display the seating map with GridBagLayout for better control
//        JPanel seatMapPanel = new JPanel();
//        seatMapPanel.setLayout(new GridBagLayout());  // Using GridBagLayout for more flexible positioning
//
//        // GridBagConstraints to control layout and padding
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(10, 10, 10, 10); // Adjusted space around each seat button
//        gbc.fill = GridBagConstraints.CENTER; // Center align seat buttons within their grid cell
//
//        // Add seat buttons to the panel
//        int seatCounter = 0;
//        int maxSeatsInRow = 4; // You want 4 seats per row
//        for (Seat seat : seats) {
//            JButton seatButton = new JButton(seat.getSeatNumber());
//            seatButton.setFont(new Font("Arial", Font.PLAIN, 12));  // Adjust font size for better fit
//            seatButton.setPreferredSize(new Dimension(30, 30));  // Keep buttons smaller (tiny squares)
//
//            // Set color based on seat availability
//            if ("Available".equalsIgnoreCase(seat.getStatus())) {
//                seatButton.setBackground(Color.GREEN);
//            } else {
//                seatButton.setBackground(Color.RED);
//            }
//
//            // Action listener to handle booking and releasing seats
//            seatButton.addActionListener(e -> {
//                if ("Available".equalsIgnoreCase(seat.getStatus())) {
//                    boolean reserved = seat.reserveSeat(user.getUserId());  // Assuming user has a userId field
//                    if (reserved) {
//                        seatButton.setBackground(Color.RED);
//                        JOptionPane.showMessageDialog(frame, "Seat " + seat.getSeatNumber() + " booked successfully.");
//                    } else {
//                        JOptionPane.showMessageDialog(frame, "Failed to book seat " + seat.getSeatNumber() + ".", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } else {
//                    boolean released = seat.releaseSeat();
//                    if (released) {
//                        seatButton.setBackground(Color.GREEN);
//                        JOptionPane.showMessageDialog(frame, "Seat " + seat.getSeatNumber() + " released successfully.");
//                    } else {
//                        JOptionPane.showMessageDialog(frame, "Failed to release seat " + seat.getSeatNumber() + ".", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                }
//            });
//
//            // Check if it's the last row with fewer than 4 seats
//            if ((seatCounter / maxSeatsInRow) == (seats.size() / maxSeatsInRow)) {
//                // Calculate spaces to center the remaining seats
//                int emptySpace = (maxSeatsInRow - (seats.size() % maxSeatsInRow)) / 2;
//                gbc.gridx = emptySpace;
//            } else {
//                gbc.gridx = seatCounter % maxSeatsInRow;
//            }
//
//            // Add the seat button to the grid
//            gbc.gridy = seatCounter / maxSeatsInRow;
//
//            seatMapPanel.add(seatButton, gbc);
//
//            // Increment the seat counter
//            seatCounter++;
//        }
//
//        // Add the seat map panel to the frame
//        frame.add(seatMapPanel, BorderLayout.CENTER);
//
//        // Panel for navigation buttons (Back to User Menu and Proceed to Payment)
//        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
//        JButton backButton = new JButton("Back to User Menu");
//        backButton.addActionListener(e -> ShowtimeView.showShowtime(frame, () -> GuestView.openGuestMenu(frame)));
//
//        JButton paymentButton = new JButton("Proceed to Payment");
//        paymentButton.addActionListener(e -> {
//            // Later define the payment view logic
//            JOptionPane.showMessageDialog(frame, "Proceeding to payment view.");
//        });
//
//        buttonPanel.add(backButton);
//        buttonPanel.add(paymentButton);
//
//        // Add the navigation buttons panel to the frame
//        frame.add(buttonPanel, BorderLayout.SOUTH);
//
//        frame.revalidate();  // Revalidate the frame layout
//        frame.repaint();     // Repaint the frame to update UI
//    }
//public static void showSeatMap(JFrame frame, int showtimeId, User user) {
//    frame.getContentPane().removeAll();
//    frame.setLayout(new BorderLayout());
//
//    // Fetch the list of seats for the showtime
//    List<Seat> seats = Seat.fetchSeatsByShowtime(showtimeId);
//
//    // Create a heading panel
//    JPanel headingPanel = new JPanel();
//    JLabel headingLabel = new JLabel("Select Your Seat(s)", SwingConstants.CENTER);
//    headingLabel.setFont(new Font("Arial", Font.BOLD, 20));
//    headingLabel.setForeground(new Color(0, 51, 102)); // Dark Blue
//    headingPanel.add(headingLabel);
//    frame.add(headingPanel, BorderLayout.NORTH);
//
//    // Create the seat map panel using GridLayout
//    int rows = (int) Math.ceil(seats.size() / 4.0); // Assume 4 seats per row
//    JPanel seatMapPanel = new JPanel(new GridLayout(rows, 4, 10, 10)); // 4 seats per row, 10px gap
//
//    for (Seat seat : seats) {
//        // Create seat buttons
//        JButton seatButton = new JButton(seat.getSeatNumber());
//        seatButton.setFont(new Font("Arial", Font.PLAIN, 14));
//        seatButton.setPreferredSize(new Dimension(80, 50)); // Standard size for buttons
//
//        // Style buttons based on availability
//        if ("Available".equalsIgnoreCase(seat.getStatus())) {
//            seatButton.setBackground(new Color(144, 238, 144)); // Light Green for Available
//        } else {
//            seatButton.setBackground(new Color(255, 69, 0)); // Red for Booked
//            seatButton.setEnabled(false); // Disable button for booked seats
//        }
//
//        // Add action listener for seat selection
//        seatButton.addActionListener(e -> {
//            int confirm = JOptionPane.showConfirmDialog(
//                    frame,
//                    "Do you want to book seat " + seat.getSeatNumber() + "?",
//                    "Confirm Booking",
//                    JOptionPane.YES_NO_OPTION
//            );
//
//            if (confirm == JOptionPane.YES_OPTION) {
//                // Attempt to reserve the seat
//                boolean reserved = seat.reserveSeat(user.getUserId());
//                if (reserved) {
//                    seatButton.setBackground(new Color(255, 69, 0)); // Change to booked color
//                    seatButton.setEnabled(false); // Disable the button
//                    JOptionPane.showMessageDialog(frame, "Seat " + seat.getSeatNumber() + " booked successfully.");
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Failed to book seat. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });
//
//        // Add the seat button to the seat map panel
//        seatMapPanel.add(seatButton);
//    }
//
//    // Add the seat map panel to the frame
//    frame.add(seatMapPanel, BorderLayout.CENTER);
//
//    // Create navigation buttons at the bottom
//    JPanel navigationPanel = new JPanel(new GridLayout(1, 2, 20, 0));
//
//    JButton backButton = new JButton("Back to Showtime Menu");
//    backButton.addActionListener(e -> ShowtimeView.showShowtime(frame, () -> GuestView.openGuestMenu(frame)));
//
//    JButton proceedButton = new JButton("Proceed to Payment");
//    proceedButton.addActionListener(e -> {
//        // Implement payment flow logic here
//        JOptionPane.showMessageDialog(frame, "Proceeding to payment...");
//    });
//
//    navigationPanel.add(backButton);
//    navigationPanel.add(proceedButton);
//
//    frame.add(navigationPanel, BorderLayout.SOUTH);
//
//    frame.revalidate();
//    frame.repaint();
//}


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
