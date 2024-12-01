package Boundary;
import Visual.*;
import Domain.*;
import Database.*;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisteredUserView {

    public static void openRegisteredUserMenu(JFrame frame, RegisteredUser loggedInUser) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JLabel registeredUserLabel = VisualGui.createStyledTitle("Registered User Menu");
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JButton viewTheatreButton = VisualGui.createStyledButton("View Theatres", () ->
                TheatreView.showTheatre(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        JButton viewMovieButton = VisualGui.createStyledButton("View Movies", () ->
                MovieView.showMovie(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        JButton viewEarlyMovieButton = VisualGui.createStyledButton("View Early Access Movies", () ->
                MovieView.showEarlyAccessMovies(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        JButton viewShowtimeButton = VisualGui.createStyledButton("View Showtimes", () ->
                ShowtimeView.showShowtimeDetails(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        JButton purchaseTicketButton = VisualGui.createStyledButton("Purchase Ticket", () ->
                PurchaseTicketViewRU.showPurchaseTicketView(frame, loggedInUser, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        JButton cancelTicketButton = VisualGui.createStyledButton("Cancel Ticket", () ->
                CancelTicketViewRU.showCancelTicketView(frame, loggedInUser, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        JButton annualFeeButton = VisualGui.createStyledButton("Pay Annual Fee", () -> handleAnnualFeePayment(frame, loggedInUser));

        JButton showTicketsButton = VisualGui.createStyledButton("View Purchased Tickets", () -> {
            String ticketInfo = loggedInUser.fetchTicketsFromDatabase();
            JOptionPane.showMessageDialog(frame,
                    ticketInfo != null && !ticketInfo.isEmpty() ? ticketInfo : "No tickets found.",
                    "Your Tickets", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton showReceiptsButton = VisualGui.createStyledButton("View Receipts", () -> {
            String receiptInfo = loggedInUser.fetchReceiptsFromDatabase();
            JOptionPane.showMessageDialog(frame,
                    receiptInfo != null && !receiptInfo.isEmpty() ? receiptInfo : "No receipts found.",
                    "Your Receipts", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton newsButton = VisualGui.createStyledButton("News", () ->
                NewsView.showNews(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        JButton informationButton = VisualGui.createStyledButton("Change Information", () ->
                ChangeInformationView.showChangeInformation(frame, loggedInUser, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        JButton backButton = VisualGui.createStyledButton("Logout", () -> {
            JOptionPane.showMessageDialog(frame, "You have been logged out.");
            WelcomeView.showMainMenu();
        });

        gbc.gridy = 0; buttonPanel.add(viewTheatreButton, gbc);
        gbc.gridy++;   buttonPanel.add(viewMovieButton, gbc);
        gbc.gridy++;   buttonPanel.add(viewEarlyMovieButton, gbc);
        gbc.gridy++;   buttonPanel.add(viewShowtimeButton, gbc);
        gbc.gridy++;   buttonPanel.add(purchaseTicketButton, gbc);
        gbc.gridy++;   buttonPanel.add(cancelTicketButton, gbc);
        gbc.gridy++;   buttonPanel.add(annualFeeButton, gbc);
        gbc.gridy++;   buttonPanel.add(showTicketsButton, gbc);
        gbc.gridy++;   buttonPanel.add(showReceiptsButton, gbc);
        gbc.gridy++;   buttonPanel.add(newsButton, gbc);
        gbc.gridy++;   buttonPanel.add(informationButton, gbc);
        gbc.gridy++;   buttonPanel.add(backButton, gbc);

        frame.add(registeredUserLabel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public static void login(JFrame frame) {
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Email:", emailField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Registered User Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            RegisteredUser loggedInUser = RegisteredUser.authenticate(email, password);
            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful! Welcome, " + loggedInUser.getName());
                openRegisteredUserMenu(frame, loggedInUser);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void handleAnnualFeePayment(JFrame frame, RegisteredUser loggedInUser) {
        if (loggedInUser.isAnnualFeePaid()) {
            JOptionPane.showMessageDialog(frame, "You have already paid the annual fee.", "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int option = JOptionPane.showConfirmDialog(
                    frame,
                    "The annual fee is $20.00. Would you like to proceed with the payment?",
                    "Annual Fee Payment",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                JTextField cardNumberField = new JTextField();
                JPasswordField cvvField = new JPasswordField();
                JTextField expirationDateField = new JTextField();

                Object[] paymentMessage = {
                        "Card Number:", cardNumberField,
                        "CVV:", cvvField,
                        "Expiration Date (MM/YY):", expirationDateField
                };

                int paymentOption = JOptionPane.showConfirmDialog(
                        frame,
                        paymentMessage,
                        "Enter Payment Details",
                        JOptionPane.OK_CANCEL_OPTION
                );

                if (paymentOption == JOptionPane.OK_OPTION) {
                    String cardNumber = cardNumberField.getText();
                    String cvv = new String(cvvField.getPassword());
                    String expirationDate = expirationDateField.getText();

                    if (validatePaymentInputs(cardNumber, cvv, expirationDate)) {
                        if (processAnnualFeePayment(cardNumber, cvv, expirationDate)) {
                            loggedInUser.setAnnualFeePaid(true);
                            updateAnnualFeeStatusInDatabase(loggedInUser);
                            JOptionPane.showMessageDialog(frame, "Annual fee payment successful. Thank you!", "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Payment failed. Please check your details or try again.", "Payment Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid payment details. Please try again.", "Invalid Details", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private static boolean validatePaymentInputs(String cardNumber, String cvv, String expirationDate) {
        return cardNumber != null && cardNumber.matches("\\d{16}")
                && cvv != null && cvv.matches("\\d{3}")
                && expirationDate != null && expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}");
    }

    private static boolean processAnnualFeePayment(String cardNumber, String cvv, String expirationDate) {
        String query = "SELECT balance FROM Bank WHERE card_number = ? AND cvv = ? AND expiration_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cardNumber);
            stmt.setString(2, cvv);
            stmt.setString(3, expirationDate);

            var rs = stmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= 20.00) {
                    String updateQuery = "UPDATE Bank SET balance = balance - 20.00 WHERE card_number = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, cardNumber);
                        updateStmt.executeUpdate();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error processing annual fee payment: " + e.getMessage());
        }
        return false;
    }

    private static void updateAnnualFeeStatusInDatabase(RegisteredUser user) {
        String query = "UPDATE Users SET annual_fee_paid = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBoolean(1, true);
            stmt.setInt(2, user.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating annual fee status: " + e.getMessage());
        }
    }
}