import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Ticket {
    private int ticketId;
    private int userId;
    private int showtimeId;
    private int seatId;
    private double price;
    private String status;
    private LocalDateTime purchaseDate;

    public Ticket(int userId, int showtimeId, int seatId, double price, String status, LocalDateTime purchaseDate) {
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatId = seatId;
        this.price = price;
        this.status = status;
        this.purchaseDate = purchaseDate;
    }

    // Save ticket to the database
    public boolean saveToDatabase() {
        String query = "INSERT INTO Tickets (user_id, showtime_id, seat_id, price, status, purchase_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, showtimeId);
            preparedStatement.setInt(3, seatId);
            preparedStatement.setDouble(4, price);
            preparedStatement.setString(5, status);
            preparedStatement.setTimestamp(6, java.sql.Timestamp.valueOf(purchaseDate));

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving ticket: " + e.getMessage());
            return false;
        }
    }
}
