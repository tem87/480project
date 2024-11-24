import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PaymentViewRU {

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

        // Voucher Details
        JPanel voucherPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        voucherPanel.setBorder(BorderFactory.createTitledBorder("Voucher Details"));

        JLabel voucherLabel = new JLabel("Select a voucher to apply:");
        JComboBox<String> voucherDropdown = new JComboBox<>();
        JLabel voucherMessage = new JLabel("No voucher applied.", SwingConstants.CENTER);

        // Fetch available vouchers for the user
        List<Integer> voucherIds = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT voucher_id, amount FROM Voucher WHERE user_id = ? AND is_used = FALSE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, loggedInUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int voucherId = rs.getInt("voucher_id");
                double amount = rs.getDouble("amount");

                voucherDropdown.addItem("Voucher #" + voucherId + " - $" + amount);
                voucherIds.add(voucherId);
            }

            if (voucherIds.isEmpty()) {
                voucherMessage.setText("No vouchers available.");
                voucherDropdown.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        voucherDropdown.addActionListener(e -> {
            if (voucherDropdown.getSelectedIndex() != -1) {
                String selectedVoucher = (String) voucherDropdown.getSelectedItem();
                assert selectedVoucher != null;
                String[] parts = selectedVoucher.split(" - \\$");
                double voucherAmount = Double.parseDouble(parts[1]);

                if (voucherAmount >= totalPriceAfterTax[0]) {
                    totalPriceAfterTax[0] = 0;
                    voucherMessage.setText("Voucher fully covers the price. Remaining: $0.00");
                } else {
                    totalPriceAfterTax[0] -= voucherAmount;
                    voucherMessage.setText(String.format("Voucher applied. Remaining to pay: $%.2f", totalPriceAfterTax[0]));
                }
                totalPriceLabel.setText(String.format("Total Price (After Tax): $%.2f", totalPriceAfterTax[0]));
            }
        });

        voucherPanel.add(voucherLabel);
        voucherPanel.add(voucherDropdown);
        voucherPanel.add(voucherMessage);
        mainPanel.add(voucherPanel);

        // Payment Information
        JPanel paymentPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));

        JComboBox<String> paymentMethodDropdown = new JComboBox<>(new String[]{"Credit Card", "Debit Card"});
        JTextField cardNumberField = new JTextField();
        JPasswordField cvvField = new JPasswordField();
        JTextField expirationDateField = new JTextField();

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
        JButton backButton = new JButton("Back");

        confirmButton.addActionListener(e -> {
            String cardNumber = cardNumberField.getText();
            String cvv = new String(cvvField.getPassword());
            String expirationDate = expirationDateField.getText();

            if (totalPriceAfterTax[0] == 0 || validateInputs(cardNumber, cvv, expirationDate)) {
                if (totalPriceAfterTax[0] == 0 || validateCardDetails(cardNumber, cvv, expirationDate, totalPriceAfterTax[0])) {
                    boolean success = true;

                    // Update voucher as used
                    if (voucherDropdown.getSelectedIndex() != -1) {
                        try (Connection conn = DBConnection.getConnection()) {
                            String updateVoucherQuery = "UPDATE Voucher SET is_used = TRUE WHERE voucher_id = ?";
                            PreparedStatement stmt = conn.prepareStatement(updateVoucherQuery);
                            stmt.setInt(1, voucherIds.get(voucherDropdown.getSelectedIndex()));
                            stmt.executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }

                    for (Seat seat : selectedSeats) {
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
}
