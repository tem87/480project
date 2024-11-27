import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    // Constructor for creating a ticket object with all fields
    public Ticket(int ticketId, int userId, int showtimeId, int seatId, double price, String status, LocalDateTime purchaseDate) {
        this.ticketId = ticketId;
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
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, showtimeId);
            preparedStatement.setInt(3, seatId);
            preparedStatement.setDouble(4, price);
            preparedStatement.setString(5, status);
            preparedStatement.setTimestamp(6, java.sql.Timestamp.valueOf(purchaseDate));

            int rowsAffected = preparedStatement.executeUpdate();

            // Retrieve the generated ticket ID
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.ticketId = generatedKeys.getInt(1); // Set ticketId
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error saving ticket: " + e.getMessage());
        }
        return false;
    }

    // Getters
    public int getTicketId() {
        return ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public int getSeatId() {
        return seatId;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    // Update ticket status in the database
    public boolean updateStatus(String newStatus) {
        String query = "UPDATE Tickets SET status = ? WHERE ticket_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, ticketId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                this.status = newStatus; // Update the object status
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating ticket status: " + e.getMessage());
        }
        return false;
    }

    // Fetch ticket from database by ID
    public static Ticket fetchTicketById(int ticketId) {
        String query = "SELECT * FROM Tickets WHERE ticket_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, ticketId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Ticket(
                        resultSet.getInt("ticket_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("showtime_id"),
                        resultSet.getInt("seat_id"),
                        resultSet.getDouble("price"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("purchase_date").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching ticket: " + e.getMessage());
        }
        return null; // Return null if not found
    }
}

