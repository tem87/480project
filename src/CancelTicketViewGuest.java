import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CancelTicketViewGuest {

    public static void showCancelTicketView(JFrame frame, Runnable backCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Prompt for guest email
        JPanel emailPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JLabel emailLabel = new JLabel("Enter the email used to purchase the ticket:");
        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 24));
        JButton checkEmailButton = new JButton("Check Email");

        emailPanel.add(emailLabel);
        JPanel emailInputPanel = new JPanel();
        emailInputPanel.add(emailField);
        emailPanel.add(emailInputPanel);
        emailPanel.add(checkEmailButton);

        frame.add(emailPanel, BorderLayout.CENTER);

        checkEmailButton.addActionListener(e -> {
            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid email.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                // Check if the email exists in the Users table and is not registered
                String query = "SELECT user_id, is_registered FROM Users WHERE email = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    boolean isRegistered = rs.getBoolean("is_registered");

                    if (!isRegistered) {
                        // Proceed to cancel ticket functionality
                        User guestUser = new User(userId, "Guest", email, null, null, null, false);
                        displayCancelTicketPanel(frame, guestUser, backCallback);
                    } else {
                        JOptionPane.showMessageDialog(frame, "This email is associated with a registered user. Please log in.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Email not found in the system.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error checking email.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.revalidate();
        frame.repaint();
    }

    private static void displayCancelTicketPanel(JFrame frame, User guestUser, Runnable backCallback) {
        frame.getContentPane().removeAll();

        JPanel cancelTicketPanel = new JPanel();
        cancelTicketPanel.setLayout(new GridLayout(0, 1, 10, 10));

        JLabel titleLabel = new JLabel("Cancel Ticket", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        cancelTicketPanel.add(titleLabel);

        JLabel ticketLabel = new JLabel("Select a ticket to cancel:");

        JComboBox<String> ticketDropdown = new JComboBox<>();
        JButton cancelTicketButton = new JButton("Cancel Ticket");
        JButton backButton = new JButton("Back");

        cancelTicketPanel.add(ticketLabel);
        cancelTicketPanel.add(ticketDropdown);
        cancelTicketPanel.add(cancelTicketButton);
        cancelTicketPanel.add(backButton);

        frame.add(cancelTicketPanel, BorderLayout.CENTER);

        // Fetch booked tickets for the guest user
        List<Integer> ticketIds = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT t.ticket_id, m.title, s.seat_number, sh.start_time " +
                    "FROM Tickets t " +
                    "JOIN Showtime sh ON t.showtime_id = sh.showtime_id " +
                    "JOIN Movie m ON sh.movie_id = m.movie_id " +
                    "JOIN Seats s ON t.seat_id = s.seat_id " +
                    "WHERE t.user_id = ? AND t.status = 'Booked'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, guestUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int ticketId = rs.getInt("ticket_id");
                String movieTitle = rs.getString("title");
                String seatNumber = rs.getString("seat_number");
                Timestamp startTime = rs.getTimestamp("start_time");

                String ticketInfo = "ID: " + ticketId + " | Movie: " + movieTitle +
                        " | Seat: " + seatNumber + " | Showtime: " + startTime;
                ticketDropdown.addItem(ticketInfo);
                ticketIds.add(ticketId);
            }

            if (ticketIds.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No tickets to cancel.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching tickets.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        cancelTicketButton.addActionListener(e -> {
            int selectedIndex = ticketDropdown.getSelectedIndex();
            if (selectedIndex == -1 || ticketIds.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select a ticket to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int ticketId = ticketIds.get(selectedIndex);

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);

                String updateSeatQuery = "UPDATE Seats SET status = 'Available' " +
                        "WHERE seat_id = (SELECT seat_id FROM Tickets WHERE ticket_id = ?)";
                PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatQuery);
                updateSeatStmt.setInt(1, ticketId);
                updateSeatStmt.executeUpdate();

                String updateTicketQuery = "UPDATE Tickets SET status = 'Cancelled' WHERE ticket_id = ?";
                PreparedStatement updateTicketStmt = conn.prepareStatement(updateTicketQuery);
                updateTicketStmt.setInt(1, ticketId);
                int rowsAffected = updateTicketStmt.executeUpdate();

                if (rowsAffected > 0) {
                    String createVoucherQuery = "INSERT INTO Voucher (user_id, amount) " +
                            "SELECT user_id, price * 0.85 FROM Tickets WHERE ticket_id = ?";
                    PreparedStatement createVoucherStmt = conn.prepareStatement(createVoucherQuery, Statement.RETURN_GENERATED_KEYS);
                    createVoucherStmt.setInt(1, ticketId);
                    createVoucherStmt.executeUpdate();

                    ResultSet generatedKeys = createVoucherStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int voucherId = generatedKeys.getInt(1);

                        String getVoucherDetailsQuery = "SELECT amount FROM Voucher WHERE voucher_id = ?";
                        PreparedStatement getVoucherDetailsStmt = conn.prepareStatement(getVoucherDetailsQuery);
                        getVoucherDetailsStmt.setInt(1, voucherId);
                        ResultSet voucherDetailsRs = getVoucherDetailsStmt.executeQuery();

                        if (voucherDetailsRs.next()) {
                            double voucherAmount = voucherDetailsRs.getDouble("amount");

                            JOptionPane.showMessageDialog(frame,
                                    "Your ticket has been successfully canceled.\n" +
                                            "Voucher Code: " + voucherId + "\n" +
                                            "Refund Amount (after 15% fee): $" + String.format("%.2f", voucherAmount),
                                    "Voucher Issued",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                    conn.commit();
                    ticketDropdown.removeItemAt(selectedIndex);
                    ticketIds.remove(selectedIndex);
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(frame, "Failed to cancel ticket. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error cancelling ticket.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> backCallback.run());

        frame.revalidate();
        frame.repaint();
    }
}
