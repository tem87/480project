import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentView {

    public static void showPaymentView(JFrame frame, Theatre theatre, Movie movie, Showtime showtime, List<Seat> selectedSeats, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout(10, 10));

        JLabel mainTitle = new JLabel("Enter Payment Details", SwingConstants.CENTER);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(mainTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
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

        JPanel pricingPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        pricingPanel.setBorder(BorderFactory.createTitledBorder("Pricing Details"));

        double pricePerSeat = movie.getPrice();
        double totalPrice = pricePerSeat * selectedSeats.size();
        double tax = totalPrice * 0.05; // 5% tax
        double totalPriceAfterTax = totalPrice + tax;

        pricingPanel.add(new JLabel(String.format("Price Per Seat: $%.2f", pricePerSeat)));
        pricingPanel.add(new JLabel(String.format("Total Price (Before Tax): $%.2f", totalPrice)));
        pricingPanel.add(new JLabel(String.format("Tax (5%%): $%.2f", tax)));
        pricingPanel.add(new JLabel(String.format("Total Price (After Tax): $%.2f", totalPriceAfterTax)));
        mainPanel.add(pricingPanel);

        JPanel paymentPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));

        JComboBox<String> paymentMethodDropdown = new JComboBox<>(new String[]{"Credit Card", "Debit Card"});
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField cardNumberField = new JTextField();
        JPasswordField cvvField = new JPasswordField();
        JTextField expirationDateField = new JTextField();

        paymentPanel.add(new JLabel("Payment Method:"));
        paymentPanel.add(paymentMethodDropdown);
        paymentPanel.add(new JLabel("Name:"));
        paymentPanel.add(nameField);
        paymentPanel.add(new JLabel("Email:"));
        paymentPanel.add(emailField);
        paymentPanel.add(new JLabel("Card Number:"));
        paymentPanel.add(cardNumberField);
        paymentPanel.add(new JLabel("CVV:"));
        paymentPanel.add(cvvField);
        paymentPanel.add(new JLabel("Expiration Date (MM/YY):"));
        paymentPanel.add(expirationDateField);
        mainPanel.add(paymentPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Confirm Payment");

        confirmButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String cardNumber = cardNumberField.getText();
            String cvv = new String(cvvField.getPassword());
            String expirationDate = expirationDateField.getText();

            if (validateInputs(name, cardNumber, cvv, expirationDate)) {
                if (validateCardDetails(cardNumber, cvv, expirationDate, totalPriceAfterTax)) {
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
                                userId, // Use the newly created user ID
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

                        seat.reserveSeat(seat.getSeatId()); // Reserve the seat
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


        JButton backButton = new JButton("Back");
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
                    // Deduct balance
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
            System.err.println("Error validating card details: " + e.getMessage());
        }
        return false;
    }
}