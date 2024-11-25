import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CancelTicketViewGuest {

    public static void showCancelTicketView(JFrame frame, Runnable backCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Prompt for guest email and name
        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JLabel emailLabel = new JLabel("Enter the email used to purchase the ticket:");
        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 24));

        JLabel nameLabel = new JLabel("Enter the name used to purchase the ticket:");
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 24));

        JButton checkDetailsButton = new JButton("Check Details");
        checkDetailsButton.setPreferredSize(new Dimension(120, 30)); // Resized button

        JButton backButton = new JButton("Back to Menu");
        backButton.setPreferredSize(new Dimension(120, 30)); // Resized button

        inputPanel.add(emailLabel);
        JPanel emailInputPanel = new JPanel();
        emailInputPanel.add(emailField);
        inputPanel.add(emailInputPanel);

        inputPanel.add(nameLabel);
        JPanel nameInputPanel = new JPanel();
        nameInputPanel.add(nameField);
        inputPanel.add(nameInputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(checkDetailsButton);
        buttonPanel.add(backButton);

        inputPanel.add(buttonPanel);

        frame.add(inputPanel, BorderLayout.CENTER);

        checkDetailsButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String name = nameField.getText().trim();

            if (email.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both a valid email and name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                // Check if the email and name exist in the Users table and the user is not registered
                String query = "SELECT user_id, is_registered FROM Users WHERE email = ? AND name = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, name);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    boolean isRegistered = rs.getBoolean("is_registered");

                    if (!isRegistered) {
                        // Proceed to cancel ticket functionality
                        User guestUser = new User(userId, name, email, null, null, null, false);
                        displayCancelTicketPanel(frame, guestUser, backCallback);
                    } else {
                        JOptionPane.showMessageDialog(frame, "This email and name are associated with a registered user. Please log in.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "No matching guest user found with the provided email and name.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error checking details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> backCallback.run());

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

                // Redirect back to the email and name prompt instead of staying in the ticket selection
                showCancelTicketView(frame, backCallback);
                return; // Exit the current method to prevent further processing
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching tickets.", "Error", JOptionPane.ERROR_MESSAGE);
            showCancelTicketView(frame, backCallback); // Return to email and name prompt on error
            return;
        }


        cancelTicketButton.addActionListener(e -> {
            int selectedIndex = ticketDropdown.getSelectedIndex();
            if (selectedIndex == -1 || ticketIds.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select a ticket to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int ticketId = ticketIds.get(selectedIndex);
            Connection conn = null;

            try {
                conn = DBConnection.getConnection(); // Get the connection
                conn.setAutoCommit(false); // Begin transaction

                // Get the showtime for the selected ticket
                String showtimeQuery = "SELECT sh.start_time FROM Tickets t " +
                        "JOIN Showtime sh ON t.showtime_id = sh.showtime_id WHERE t.ticket_id = ?";
                PreparedStatement showtimeStmt = conn.prepareStatement(showtimeQuery);
                showtimeStmt.setInt(1, ticketId);
                ResultSet rs = showtimeStmt.executeQuery();

                if (rs.next()) {
                    Timestamp showtime = rs.getTimestamp("start_time");
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                    // Calculate the difference in hours between current time and showtime
                    long timeDifference = showtime.getTime() - currentTime.getTime();
                    long hoursDifference = timeDifference / (1000 * 60 * 60); // Convert milliseconds to hours

                    // If showtime is within 72 hours, ticket can't be canceled
                    if (hoursDifference < 72) {
                        JOptionPane.showMessageDialog(frame, "Tickets can only be canceled more than 72 hours before the showtime.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Showtime details not found for the selected ticket.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Proceed with the ticket cancellation
                String updateSeatQuery = "UPDATE Seats SET status = 'Available' WHERE seat_id = (SELECT seat_id FROM Tickets WHERE ticket_id = ?)";
                PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatQuery);
                updateSeatStmt.setInt(1, ticketId);
                updateSeatStmt.executeUpdate();

                String updateTicketQuery = "UPDATE Tickets SET status = 'Cancelled' WHERE ticket_id = ?";
                PreparedStatement updateTicketStmt = conn.prepareStatement(updateTicketQuery);
                updateTicketStmt.setInt(1, ticketId);
                int rowsAffected = updateTicketStmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Create a voucher for the canceled ticket
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
                            JOptionPane.showMessageDialog(frame, "Ticket successfully canceled. Voucher created for $" + voucherAmount, "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                    conn.commit(); // Commit the transaction
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to cancel ticket. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                try {
                    if (conn != null) {
                        conn.rollback(); // Rollback in case of error
                    }
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                JOptionPane.showMessageDialog(frame, "Error processing cancellation.", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (conn != null) {
                        conn.close(); // Close connection
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });


        backButton.addActionListener(e -> backCallback.run());

        frame.revalidate();
        frame.repaint();
    }

}