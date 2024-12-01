package Domain;

import Database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisteredUser extends User {
    private boolean annualFeePaid;

    // ctor for creating a new ru
    public RegisteredUser(String name, String email, String password, String phoneNumber, String address) {
        super(name, email, password, phoneNumber, address);
        this.annualFeePaid = false;
    }

    // ctor for existing ru
    public RegisteredUser(int userId, String name, String email, String password, String phoneNumber, String address,
                          boolean annualFeePaid) {
        super(userId, name, email, password, phoneNumber, address, true);
        this.annualFeePaid = annualFeePaid;
    }

    public boolean isAnnualFeePaid() {
        return annualFeePaid;
    }

    public void setAnnualFeePaid(boolean annualFeePaid) {
        this.annualFeePaid = annualFeePaid;
    }

    @Override
    public boolean saveToDatabase() {
        String query = "INSERT INTO Users (name, email, password, phone_number, address, is_registered, annual_fee_paid) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, getName());
            preparedStatement.setString(2, getEmail());
            preparedStatement.setString(3, getPassword());
            preparedStatement.setString(4, getPhoneNumber());
            preparedStatement.setString(5, getAddress());
            preparedStatement.setBoolean(6, true); // is_registered is true
            preparedStatement.setBoolean(7, annualFeePaid);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving registered user: " + e.getMessage());
            return false;
        }
    }

    public static RegisteredUser authenticate(String email, String password) {
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next() && resultSet.getBoolean("is_registered")) {
                return new RegisteredUser(
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("address"),
                        resultSet.getBoolean("annual_fee_paid")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }

        return null;
    }

//    public String fetchReceiptsFromDatabase() {
//        StringBuilder receiptInfo = new StringBuilder();
//        String query = "SELECT r.payment_id, r.ticket_id, r.total_amount, r.payment_date, r.masked_card_number " +
//                "FROM Receipt r " +
//                "WHERE r.user_id = ?";
//
//        try (Connection conn = Database.DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setInt(1, this.getUserId());
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                int paymentId = rs.getInt("payment_id");
//                int ticketId = rs.getInt("ticket_id");
//                double totalAmount = rs.getDouble("total_amount");
//                String paymentDate = rs.getTimestamp("payment_date").toString();
//                String maskedCardNumber = rs.getString("masked_card_number");
//
//                receiptInfo.append("Payment ID: ").append(paymentId).append("\n")
//                        .append("Ticket ID: ").append(ticketId).append("\n")
//                        .append("Amount Paid: $").append(String.format("%.2f", totalAmount)).append("\n")
//                        .append("Payment Date: ").append(paymentDate).append("\n")
//                        .append("Card: ").append(maskedCardNumber).append("\n\n");
//            }
//        } catch (SQLException e) {
//            System.err.println("Error fetching receipts: " + e.getMessage());
//        }
//
//        return receiptInfo.toString();
//    }
    public String fetchReceiptsFromDatabase() {
        StringBuilder receiptInfo = new StringBuilder();
        String query = "SELECT r.payment_id, r.ticket_id, r.total_amount, r.payment_date, r.masked_card_number, " +
                "s.seat_number, m.title, t.name " +
                "FROM Receipt r " +
                "JOIN Tickets ti ON r.ticket_id = ti.ticket_id " +
                "JOIN Seats s ON ti.seat_id = s.seat_id " +
                "JOIN Showtime st ON ti.showtime_id = st.showtime_id " +
                "JOIN Movie m ON st.movie_id = m.movie_id " +
                "JOIN Theater t ON st.theater_id = t.theater_id " +
                "WHERE r.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, this.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int paymentId = rs.getInt("payment_id");
                int ticketId = rs.getInt("ticket_id");
                double totalAmount = rs.getDouble("total_amount");
                String paymentDate = rs.getTimestamp("payment_date").toString();
                String maskedCardNumber = rs.getString("masked_card_number");
                String seatNumber = rs.getString("seat_number");
                String movieTitle = rs.getString("title");
                String theatreName = rs.getString("name");

                receiptInfo.append("Payment ID: ").append(paymentId).append("\n")
                        .append("Ticket ID: ").append(ticketId).append("\n")
                        .append("Amount Paid: $").append(String.format("%.2f", totalAmount)).append("\n")
                        .append("Payment Date: ").append(paymentDate).append("\n")
                        .append("Card: ").append(maskedCardNumber).append("\n")
                        .append("Seat Number: ").append(seatNumber).append("\n")
                        .append("Movie: ").append(movieTitle).append("\n")
                        .append("Theatre: ").append(theatreName).append("\n\n");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching receipts: " + e.getMessage());
        }

        return receiptInfo.toString();
    }


}