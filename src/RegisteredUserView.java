import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisteredUserView {

    public static void openRegisteredUserMenu(JFrame frame, RegisteredUser loggedInUser) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel registeredUserPanel = new JPanel();
        registeredUserPanel.setLayout(new GridLayout(0, 1, 10, 10));

        JLabel registeredUserLabel = new JLabel("Registered User Menu", SwingConstants.CENTER);
        registeredUserLabel.setFont(new Font("Arial", Font.BOLD, 18));
        registeredUserPanel.add(registeredUserLabel);

        JButton viewTheatreButton = new JButton("View Theatres");
        JButton viewMovieButton = new JButton("View Movies");
        JButton viewearlyMovieButton = new JButton("View Early Access Movies");
        JButton viewShowtimeButton = new JButton("View Showtimes");
        JButton purchaseTicketButton = new JButton("Purchase Ticket");
        JButton cancelTicketButton = new JButton("Cancel Ticket");
        JButton annualFeeButton = new JButton("Pay Annual Fee");
        JButton newsButton = new JButton("News");
        JButton informationButton = new JButton("Change Information");
        JButton showTicketsButton = new JButton("View Purchased Tickets");
        JButton showReceiptsButton = new JButton("View Receipts");
        JButton backButton = new JButton("Logout");



        viewTheatreButton.addActionListener(e ->
                TheatreView.showTheatre(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        viewMovieButton.addActionListener(e ->
                MovieView.showMovie(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        viewearlyMovieButton.addActionListener(e ->
                MovieView.showEarlyAccessMovies(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        viewShowtimeButton.addActionListener(e ->
                ShowtimeView.showShowtimeDetails(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        purchaseTicketButton.addActionListener(e ->
                PurchaseTicketViewRU.showPurchaseTicketView(frame, loggedInUser, () -> RegisteredUserView.openRegisteredUserMenu(frame, loggedInUser))
        );

        annualFeeButton.addActionListener(e -> handleAnnualFeePayment(frame, loggedInUser));

        // Update callbacks for the registered user functionality
        showTicketsButton.addActionListener(e -> {
            String ticketInfo = loggedInUser.fetchTicketsFromDatabase();
            if (ticketInfo != null && !ticketInfo.isEmpty()) {
                JOptionPane.showMessageDialog(frame, ticketInfo, "Your Tickets", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "No tickets found for your account.", "Your Tickets", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        showReceiptsButton.addActionListener(e -> {
            String receiptInfo = loggedInUser.fetchReceiptsFromDatabase();
            if (receiptInfo != null && !receiptInfo.isEmpty()) {
                JOptionPane.showMessageDialog(frame, receiptInfo, "Your Receipts", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "No receipts found for your account.", "Your Receipts", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        cancelTicketButton.addActionListener(e ->
                CancelTicketViewRU.showCancelTicketView(frame, loggedInUser, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        // Navigate to ChangeInformationView
        informationButton.addActionListener(e ->
                ChangeInformationView.showChangeInformation(frame, loggedInUser, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        // Logout functionality
        backButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "You have been logged out.");
            WelcomeView.showMainMenu();
        });


        registeredUserPanel.add(viewTheatreButton);
        registeredUserPanel.add(viewMovieButton);
        registeredUserPanel.add(viewearlyMovieButton);
        registeredUserPanel.add(viewShowtimeButton);
        registeredUserPanel.add(purchaseTicketButton);
        registeredUserPanel.add(cancelTicketButton);
        registeredUserPanel.add(annualFeeButton);
        registeredUserPanel.add(showTicketsButton); // Add ticket buttons
        registeredUserPanel.add(showReceiptsButton);
        registeredUserPanel.add(newsButton);
        registeredUserPanel.add(informationButton);
        registeredUserPanel.add(backButton);

        frame.add(registeredUserPanel, BorderLayout.CENTER);
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