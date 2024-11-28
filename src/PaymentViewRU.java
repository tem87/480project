import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentViewRU {
    static boolean success = false; //used to track if payment was successful
    public static void showPaymentViewRU(JFrame frame, RegisteredUser loggedInUser, Theatre theatre, Movie movie, Showtime showtime, List<Seat> selectedSeats, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout(10, 10));

        JLabel mainTitle = new JLabel("Payment Details for " + loggedInUser.getName(), SwingConstants.CENTER);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(mainTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        // Movie and Seat Details
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Movie & Seat Details"));

        StringBuilder seatNumbers = new StringBuilder();
        for (Seat seat : selectedSeats) {
            seatNumbers.append(seat.getSeatNumber()).append(", ");
        }

        detailsPanel.add(new JLabel("Movie: " + movie.getTitle()));
        detailsPanel.add(new JLabel("Showtime: " + showtime.getDateTime()));
        detailsPanel.add(new JLabel("Selected Seats: " + seatNumbers.toString()));
        mainPanel.add(detailsPanel);

        // Pricing Details
        JPanel pricingPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        pricingPanel.setBorder(BorderFactory.createTitledBorder("Pricing Details"));

        double pricePerSeat = movie.getPrice();
        double totalPrice = pricePerSeat * selectedSeats.size();
        double tax = totalPrice * 0.05; // 5% tax
        final double[] totalPriceAfterTax = {totalPrice + tax}; // Mutable wrapper for the total price

        JLabel totalPriceLabel = new JLabel(String.format("Total Price (After Tax): $%.2f", totalPriceAfterTax[0]));
        pricingPanel.add(new JLabel(String.format("Price Per Seat: $%.2f", pricePerSeat)));
        pricingPanel.add(new JLabel(String.format("Total Price (Before Tax): $%.2f", totalPrice)));
        pricingPanel.add(new JLabel(String.format("Tax (5%%): $%.2f", tax)));
        pricingPanel.add(totalPriceLabel);
        mainPanel.add(pricingPanel);

        // Voucher Input
        JPanel voucherPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        voucherPanel.setBorder(BorderFactory.createTitledBorder("Voucher Code"));

        JLabel voucherLabel = new JLabel("Enter Voucher Code:");
        JTextField voucherField = new JTextField();
        JButton applyVoucherButton = new JButton("Apply Voucher");
        JLabel voucherMessage = new JLabel("No voucher applied.", SwingConstants.CENTER);

        applyVoucherButton.addActionListener(e -> {
            String voucherCode = voucherField.getText().trim();

            if (voucherCode.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid voucher code.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT amount, created_at, is_used FROM Voucher WHERE voucher_id = ? AND user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, voucherCode);
                stmt.setInt(2, loggedInUser.getUserId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    boolean isUsed = rs.getBoolean("is_used");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    double voucherAmount = rs.getDouble("amount");

                    // Check if the voucher is already used
                    if (isUsed) {
                        JOptionPane.showMessageDialog(frame, "This voucher has already been used.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Check if the voucher is within one year of its creation date
                    LocalDateTime creationDate = createdAt.toLocalDateTime();
                    LocalDateTime expiryDate = creationDate.plusYears(1);
                    if (LocalDateTime.now().isAfter(expiryDate)) {
                        JOptionPane.showMessageDialog(frame, "This voucher has expired.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Apply the voucher if valid
                    if (voucherAmount >= totalPriceAfterTax[0]) {
                        totalPriceAfterTax[0] = 0;
                        voucherMessage.setText("Voucher fully covers the price. Remaining: $0.00");
                    } else {
                        totalPriceAfterTax[0] -= voucherAmount;
                        voucherMessage.setText(String.format("Voucher applied. Remaining to pay: $%.2f", totalPriceAfterTax[0]));
                    }
                    totalPriceLabel.setText(String.format("Total Price (After Tax): $%.2f", totalPriceAfterTax[0]));

                    // Mark the voucher as used
                    String updateVoucherQuery = "UPDATE Voucher SET is_used = TRUE WHERE voucher_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateVoucherQuery);
                    updateStmt.setString(1, voucherCode);
                    updateStmt.executeUpdate();

                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid voucher code.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error applying voucher.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        voucherPanel.add(voucherLabel);
        voucherPanel.add(voucherField);
        voucherPanel.add(applyVoucherButton);
        voucherPanel.add(voucherMessage);
        mainPanel.add(voucherPanel);

        // Payment Information
        JPanel paymentPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));

        JComboBox<String> paymentMethodDropdown = new JComboBox<>(new String[]{"Credit Card"});
        JTextField cardNumberField = new JTextField();
        JPasswordField cvvField = new JPasswordField();
        JTextField expirationDateField = new JTextField();

        restrictToDigits(cardNumberField, 16);
        restrictToDigits(cvvField, 3);

        paymentPanel.add(new JLabel("Payment Method:"));
        paymentPanel.add(paymentMethodDropdown);
        paymentPanel.add(new JLabel("Card Number:"));
        paymentPanel.add(cardNumberField);
        paymentPanel.add(new JLabel("CVV:"));
        paymentPanel.add(cvvField);
        paymentPanel.add(new JLabel("Expiration Date (MM/YY):"));
        paymentPanel.add(expirationDateField);
        mainPanel.add(paymentPanel);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Confirm Payment");
        //email thing
        JButton sendEmailButton = new JButton("Send Receipt and Tickets via Email");
        JButton backButton = new JButton("Back");

        confirmButton.addActionListener(e -> {
            String cardNumber = cardNumberField.getText();
            String cvv = new String(cvvField.getPassword());
            String expirationDate = expirationDateField.getText();

            if (totalPriceAfterTax[0] == 0 || validateInputs(cardNumber, cvv, expirationDate)) {
                if (totalPriceAfterTax[0] == 0 || validateCardDetails(cardNumber, cvv, expirationDate, totalPriceAfterTax[0])) {
                    confirmButton.setEnabled(false); // Disable the button only after successful validation
                    success = true; //make global

                    try (Connection conn = DBConnection.getConnection()) {
                        conn.setAutoCommit(false);

                        // Check if this is an early access movie
                        String checkEarlyAccessQuery = "SELECT early_access FROM Movie WHERE movie_id = ?";
                        boolean isEarlyAccess = false;

                        try (PreparedStatement checkStmt = conn.prepareStatement(checkEarlyAccessQuery)) {
                            checkStmt.setInt(1, movie.getMovieId());
                            ResultSet rs = checkStmt.executeQuery();
                            if (rs.next()) {
                                isEarlyAccess = rs.getBoolean("early_access");
                            }
                        }

                        if (isEarlyAccess) {
                            // Get the total number of seats for the showtime
                            String totalSeatsQuery = "SELECT COUNT(*) AS total_seats FROM Seats WHERE showtime_id = ?";
                            int totalSeats = 0;

                            try (PreparedStatement totalSeatsStmt = conn.prepareStatement(totalSeatsQuery)) {
                                totalSeatsStmt.setInt(1, showtime.getShowtimeID());
                                ResultSet rs = totalSeatsStmt.executeQuery();
                                if (rs.next()) {
                                    totalSeats = rs.getInt("total_seats");
                                }
                            }

                            // Get the number of seats already booked by registered users
                            String bookedSeatsQuery = "SELECT COUNT(*) AS booked_seats FROM Tickets t " +
                                    "JOIN Users u ON t.user_id = u.user_id " +
                                    "WHERE t.showtime_id = ? AND u.is_registered = TRUE";
                            int bookedSeats = 0;

                            try (PreparedStatement bookedSeatsStmt = conn.prepareStatement(bookedSeatsQuery)) {
                                bookedSeatsStmt.setInt(1, showtime.getShowtimeID());
                                ResultSet rs = bookedSeatsStmt.executeQuery();
                                if (rs.next()) {
                                    bookedSeats = rs.getInt("booked_seats");
                                }
                            }

                            // Calculate the maximum number of seats registered users can book
                            int maxSeatsForRegisteredUsers = (int) Math.ceil(totalSeats * 0.10);
                            if (bookedSeats + selectedSeats.size() > maxSeatsForRegisteredUsers) {
                                JOptionPane.showMessageDialog(frame,
                                        "Cannot book seats. Only 10% of the seats can be booked by registered users for early access movies.",
                                        "Booking Limit Reached",
                                        JOptionPane.ERROR_MESSAGE);
                                conn.rollback();
                                confirmButton.setEnabled(true); // Re-enable the button
                                return;
                            }
                        }

                        for (Seat seat : selectedSeats) {
                            // Check if the seat is already reserved
                            String checkSeatQuery = "SELECT status FROM Seats WHERE seat_id = ? AND showtime_id = ?";
                            try (PreparedStatement checkStmt = conn.prepareStatement(checkSeatQuery)) {
                                checkStmt.setInt(1, seat.getSeatId());
                                checkStmt.setInt(2, showtime.getShowtimeID());
                                ResultSet rs = checkStmt.executeQuery();

                                if (rs.next() && "Reserved".equalsIgnoreCase(rs.getString("status"))) {
                                    JOptionPane.showMessageDialog(frame, "Seat " + seat.getSeatNumber() + " is already reserved. Payment aborted.");
                                    conn.rollback();
                                    confirmButton.setEnabled(true); // Re-enable the button
                                    return;
                                }
                            }

                            // Create the ticket
                            Ticket ticket = new Ticket(
                                    loggedInUser.getUserId(),
                                    showtime.getShowtimeID(),
                                    seat.getSeatId(),
                                    pricePerSeat,
                                    "Booked",
                                    java.time.LocalDateTime.now()
                            );

                            if (!ticket.saveToDatabase()) {
                                success = false;
                                JOptionPane.showMessageDialog(frame, "Failed to save ticket for seat " + seat.getSeatNumber() + ". Please try again.");
                                conn.rollback();
                                break;
                            }

                            int ticketId = ticket.getTicketId(); // Fetch the generated ticket_id
                            if (ticketId == -1) {
                                success = false;
                                JOptionPane.showMessageDialog(frame, "Failed to retrieve ticket ID. Payment aborted.");
                                conn.rollback();
                                break;
                            }

                            // Save receipt linked to the ticket
                            Receipt receipt = new Receipt(
                                    loggedInUser.getUserId(),
                                    ticketId,
                                    cardNumber,
                                    LocalDateTime.now(),
                                    pricePerSeat
                            );

                            if (!receipt.saveToDatabase()) {
                                success = false;
                                JOptionPane.showMessageDialog(frame, "Failed to save receipt for ticket ID " + ticketId + ". Payment aborted.");
                                conn.rollback();
                                break;
                            }

                            // Reserve the seat
                            seat.reserveSeat(seat.getSeatId());
                        }

                        if (success) {
                            conn.commit(); // Commit transaction
                            JOptionPane.showMessageDialog(frame, "Payment Successful! Tickets and receipts have been saved.");
                            confirmButton.setEnabled(false);
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error processing payment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid or insufficient balance in card. Payment failed.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Payment Details. Please try again.");
            }
        });


        // Send Receipt and Tickets via Email Button
        sendEmailButton.addActionListener(e -> {
            if (!success) {
                JOptionPane.showMessageDialog(frame, "Payment has not been completed. Please make a payment first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT email FROM Users WHERE user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, loggedInUser.getUserId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String email = rs.getString("email");
                    JOptionPane.showMessageDialog(frame,
                            "Receipt and Tickets have been sent to: " + email,
                            "Email Sent",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Email not found for this user.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error retrieving email. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        backButton.addActionListener(e -> backToMenuCallback.run());

        buttonPanel.add(confirmButton);
        buttonPanel.add(sendEmailButton);
        buttonPanel.add(backButton);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();
    }

    private static boolean validateInputs(String cardNumber, String cvv, String expirationDate) {
        return cardNumber != null && cardNumber.matches("\\d{16}")
                && cvv != null && cvv.matches("\\d{3}")
                && expirationDate != null && expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}");
    }

    private static boolean validateCardDetails(String cardNumber, String cvv, String expirationDate, double totalPrice) {
        String query = "SELECT balance FROM Bank WHERE card_number = ? AND cvv = ? AND expiration_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cardNumber);
            stmt.setString(2, cvv);
            stmt.setString(3, expirationDate);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= totalPrice) {
                    String updateQuery = "UPDATE Bank SET balance = balance - ? WHERE card_number = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setDouble(1, totalPrice);
                        updateStmt.setString(2, cardNumber);
                        updateStmt.executeUpdate();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean associateTicketWithReceipt(int paymentId, int ticketId) {
        String query = "UPDATE Receipt SET ticket_id = ? WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, ticketId);
            stmt.setInt(2, paymentId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void restrictToDigits(JTextField field, int maxLength) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || field.getText().length() >= maxLength) {
                    e.consume(); // Ignore non-digit or excess characters
                }
            }
        });
    }


}
