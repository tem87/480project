import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class PaymentViewGuest {

    public static void showPaymentView(JFrame frame, Theatre theatre, Movie movie, Showtime showtime, List<Seat> selectedSeats, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout(10, 10));

        JLabel mainTitle = new JLabel("Enter Payment Details", SwingConstants.CENTER);
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

        // Voucher implementation
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
                String query = "SELECT amount FROM Voucher WHERE voucher_id = ? AND is_used = FALSE";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, voucherCode);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    double voucherAmount = rs.getDouble("amount");

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
                    JOptionPane.showMessageDialog(frame, "Invalid or already used voucher code.", "Error", JOptionPane.ERROR_MESSAGE);
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

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField cardNumberField = new JTextField();
        JPasswordField cvvField = new JPasswordField();
        JTextField expirationDateField = new JTextField();

        paymentPanel.add(new JLabel("Email:"));
        paymentPanel.add(emailField);
        paymentPanel.add(new JLabel("Cardholder Name:"));
        paymentPanel.add(nameField);
        paymentPanel.add(new JLabel("Card Number:"));
        paymentPanel.add(cardNumberField);
        paymentPanel.add(new JLabel("CVV:"));
        paymentPanel.add(cvvField);
        paymentPanel.add(new JLabel("Expiration Date (MM/YY):"));
        paymentPanel.add(expirationDateField);
        mainPanel.add(paymentPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Confirm Payment");
        JButton backButton = new JButton("Back");

        confirmButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String cardNumber = cardNumberField.getText();
            String cvv = new String(cvvField.getPassword());
            String expirationDate = expirationDateField.getText();

            if (validateInputs(name, cardNumber, cvv, expirationDate)) {
                if (validateCardDetails(cardNumber, cvv, expirationDate, totalPriceAfterTax[0])) {
                    // Save guest user to Users table
                    int userId = User.saveGuestUserToDatabase(name, email);
                    if (userId == -1) {
                        JOptionPane.showMessageDialog(frame, "Failed to save user details. Payment aborted.");
                        return;
                    }

                    boolean success = true;
                    for (Seat seat : selectedSeats) {
                        // Save ticket for each seat
                        Ticket ticket = new Ticket(
                                userId,
                                showtime.getShowtimeID(),
                                seat.getSeatId(),
                                pricePerSeat,
                                "Booked",
                                java.time.LocalDateTime.now()
                        );

                        if (!ticket.saveToDatabase()) {
                            success = false;
                            JOptionPane.showMessageDialog(frame, "Failed to save ticket for seat " + seat.getSeatNumber() + ". Please try again.");
                            break;
                        }

                        seat.reserveSeat(seat.getSeatId());
                    }

                    if (success) {
                        JOptionPane.showMessageDialog(frame, "Payment Successful! Tickets have been saved.");
                        backToMenuCallback.run();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid or insufficient balance in card. Payment failed.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Payment Details. Please try again.");
            }
        });

        backButton.addActionListener(e -> backToMenuCallback.run());

        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();
    }

    private static boolean validateInputs(String name, String cardNumber, String cvv, String expirationDate) {
        return name != null && !name.trim().isEmpty()
                && cardNumber != null && cardNumber.matches("\\d{16}")
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
}
