package Boundary;
import Visual.*;
import Domain.*;
import Database.*;
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

        JButton checkDetailsButton = VisualGui.createStyledButtonSmall("Check Details");
        //checkDetailsButton.setPreferredSize(new Dimension(120, 30));

        JButton backButton = VisualGui.createStyledButtonSmall("Back to Menu");
        //backButton.setPreferredSize(new Dimension(120, 30));

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
                // Chekc if the email and name exist in the Users table and the user is not registered
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
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        cancelTicketPanel.add(titleLabel);

        JLabel ticketLabel = new JLabel("Select a ticket to cancel:");

        JComboBox<String> ticketDropdown = new JComboBox<>();
        JButton cancelTicketButton = VisualGui.createStyledButtonSmall("Cancel Ticket");
        JButton backButton = VisualGui.createStyledButtonSmall("Back");

        cancelTicketPanel.add(ticketLabel);
        cancelTicketPanel.add(ticketDropdown);
        cancelTicketPanel.add(cancelTicketButton);
        cancelTicketPanel.add(backButton);

        frame.add(cancelTicketPanel, BorderLayout.CENTER);

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
                showCancelTicketView(frame, backCallback);
                return; //
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching tickets.", "Error", JOptionPane.ERROR_MESSAGE);
            showCancelTicketView(frame, backCallback);
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
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false); // Begin transaction

                String showtimeQuery = "SELECT sh.start_time FROM Tickets t " +
                        "JOIN Showtime sh ON t.showtime_id = sh.showtime_id WHERE t.ticket_id = ?";
                PreparedStatement showtimeStmt = conn.prepareStatement(showtimeQuery);
                showtimeStmt.setInt(1, ticketId);
                ResultSet rs = showtimeStmt.executeQuery();

                //check if cancellation is within allowed time
                if (rs.next()) {
                    Timestamp showtime = rs.getTimestamp("start_time");
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                    // calculate the difference in hours between current time and showtime
                    long timeDifference = showtime.getTime() - currentTime.getTime();
                    long hoursDifference = timeDifference / (1000 * 60 * 60); // Convert milliseconds to hours

                    // if showtime is within 72 hours, ticket can't be canceled
                    if (hoursDifference < 72) {
                        JOptionPane.showMessageDialog(frame, "Tickets can only be canceled more than 72 hours before the showtime.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Showtime details not found for the selected ticket.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // proceed with the ticket cancellation and update seat status
                String updateSeatQuery = "UPDATE Seats SET status = 'Available' WHERE seat_id = (SELECT seat_id FROM Tickets WHERE ticket_id = ?)";
                PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatQuery);
                updateSeatStmt.setInt(1, ticketId);
                updateSeatStmt.executeUpdate();

                // mark ticket as "cancelled"
                String updateTicketQuery = "UPDATE Tickets SET status = 'Cancelled' WHERE ticket_id = ?";
                PreparedStatement updateTicketStmt = conn.prepareStatement(updateTicketQuery);
                updateTicketStmt.setInt(1, ticketId);
                int rowsAffected = updateTicketStmt.executeUpdate();

                //generate voucher id and save it in the database
                int voucherId = generateUniqueVoucherId(conn);

                // create a voucher for the user
                String createVoucherQuery = "INSERT INTO Voucher (voucher_id, user_id, amount, created_at) " +
                        "SELECT ?, user_id, price, CURRENT_TIMESTAMP FROM Tickets WHERE ticket_id = ?";
                PreparedStatement createVoucherStmt = conn.prepareStatement(createVoucherQuery);
                createVoucherStmt.setInt(1, voucherId);
                createVoucherStmt.setInt(2, ticketId);
                createVoucherStmt.executeUpdate();

                String getVoucherDetailsQuery = "SELECT amount, created_at FROM Voucher WHERE voucher_id = ?";
                PreparedStatement getVoucherDetailsStmt = conn.prepareStatement(getVoucherDetailsQuery);
                getVoucherDetailsStmt.setInt(1, voucherId);
                ResultSet voucherDetailsRs = getVoucherDetailsStmt.executeQuery();

                if (voucherDetailsRs.next()) {
                    double voucherAmount = voucherDetailsRs.getDouble("amount");
                    Timestamp createdAt = voucherDetailsRs.getTimestamp("created_at");
                    JOptionPane.showMessageDialog(frame,
                            "Your ticket has been successfully canceled.\n" +
                                    "Voucher Code: " + voucherId + "\n" +
                                    "Refund Amount: $" + String.format("%.2f", voucherAmount) + "\n" +
                                    "Created At: " + createdAt,
                            "Voucher Issued",
                            JOptionPane.INFORMATION_MESSAGE);
                }


                if (voucherDetailsRs.next()) {
                    double voucherAmount = voucherDetailsRs.getDouble("amount");
                    JOptionPane.showMessageDialog(frame,
                            "Your ticket has been successfully canceled.\n" +
                                    "Voucher Code: " + voucherId + "\n" +
                                    "Refund Amount: $" + String.format("%.2f", voucherAmount),
                            "Voucher Issued",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                conn.commit();
                ticketDropdown.removeItemAt(selectedIndex);
                ticketIds.remove(selectedIndex);
            } catch (SQLException ex) {
                ex.printStackTrace();
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                JOptionPane.showMessageDialog(frame, "Error processing cancellation.", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
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

    private static int generateUniqueVoucherId(Connection conn) throws SQLException {
        int voucherId;
        boolean isUnique;
        do {
            voucherId = 1000 + (int) (Math.random() * 9000);
            String checkQuery = "SELECT COUNT(*) FROM Voucher WHERE voucher_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, voucherId);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                isUnique = rs.getInt(1) == 0;
            }
        } while (!isUnique);
        return voucherId;
    }

}