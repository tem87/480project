import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CancelTicketView {

    public static void showCancelTicketView(JFrame frame, RegisteredUser loggedInUser, Runnable backCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel cancelTicketPanel = new JPanel();
        cancelTicketPanel.setLayout(new GridLayout(0, 1, 10, 10));

        JLabel titleLabel = new JLabel("Cancel Ticket", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        cancelTicketPanel.add(titleLabel);

        JLabel ticketLabel = new JLabel("Select a ticket to cancel:");

        // Dropdown to display tickets
        JComboBox<String> ticketDropdown = new JComboBox<>();
        JButton cancelTicketButton = new JButton("Cancel Ticket");
        JButton backButton = new JButton("Back");

        cancelTicketPanel.add(ticketLabel);
        cancelTicketPanel.add(ticketDropdown);
        cancelTicketPanel.add(cancelTicketButton);
        cancelTicketPanel.add(backButton);

        frame.add(cancelTicketPanel, BorderLayout.CENTER);

        // Fetch booked tickets for the logged-in user
        List<Integer> ticketIds = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT t.ticket_id, m.title, s.seat_number, sh.start_time " +
                    "FROM Tickets t " +
                    "JOIN Showtime sh ON t.showtime_id = sh.showtime_id " +
                    "JOIN Movie m ON sh.movie_id = m.movie_id " +
                    "JOIN Seats s ON t.seat_id = s.seat_id " +
                    "WHERE t.user_id = ? AND t.status = 'Booked'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, loggedInUser.getUserId());
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

        // Action for "Cancel Ticket" button
        cancelTicketButton.addActionListener(e -> {
            int selectedIndex = ticketDropdown.getSelectedIndex();
            if (selectedIndex == -1 || ticketIds.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select a ticket to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int ticketId = ticketIds.get(selectedIndex);

            // Update seat status, cancel ticket, and create a voucher
            try (Connection conn = DBConnection.getConnection()) {
                // Start a transaction
                conn.setAutoCommit(false);


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


                // Step 1: Update the seat status to "Available"
                String updateSeatQuery = "UPDATE Seats SET status = 'Available' " +
                        "WHERE seat_id = (SELECT seat_id FROM Tickets WHERE ticket_id = ?)";
                PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatQuery);
                updateSeatStmt.setInt(1, ticketId);
                updateSeatStmt.executeUpdate();

                // Step 2: Mark the ticket as "Cancelled"
                String updateTicketQuery = "UPDATE Tickets SET status = 'Cancelled' WHERE ticket_id = ?";
                PreparedStatement updateTicketStmt = conn.prepareStatement(updateTicketQuery);
                updateTicketStmt.setInt(1, ticketId);
                int rowsAffected = updateTicketStmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Step 3: Create a voucher for the user
                    String createVoucherQuery = "INSERT INTO Voucher (user_id, amount) " +
                            "SELECT user_id, price FROM Tickets WHERE ticket_id = ?";
                    PreparedStatement createVoucherStmt = conn.prepareStatement(createVoucherQuery, Statement.RETURN_GENERATED_KEYS);
                    createVoucherStmt.setInt(1, ticketId);
                    createVoucherStmt.executeUpdate();

                    // Retrieve the generated voucher ID
                    ResultSet generatedKeys = createVoucherStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int voucherId = generatedKeys.getInt(1);

                        // Retrieve voucher details
                        String getVoucherDetailsQuery = "SELECT amount FROM Voucher WHERE voucher_id = ?";
                        PreparedStatement getVoucherDetailsStmt = conn.prepareStatement(getVoucherDetailsQuery);
                        getVoucherDetailsStmt.setInt(1, voucherId);
                        ResultSet voucherDetailsRs = getVoucherDetailsStmt.executeQuery();

                        if (voucherDetailsRs.next()) {
                            double voucherAmount = voucherDetailsRs.getDouble("amount");

                            // Display the voucher details to the user
                            JOptionPane.showMessageDialog(frame,
                                    "Your ticket has been successfully canceled.\n" +
                                            "Voucher Code: " + voucherId + "\n" +
                                            "Refund Amount: $" + String.format("%.2f", voucherAmount),
                                    "Voucher Issued",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                    // Commit transaction
                    conn.commit();

                    // Remove the canceled ticket from the dropdown
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

        // Action for "Back" button
        backButton.addActionListener(e -> backCallback.run());

        frame.revalidate();
        frame.repaint();
    }
}