import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Receipt {
    private int paymentId;
    private int userId;
    private int ticketId;
    private String cardNumber;
    private LocalDateTime paymentDate;
    private double totalAmount;
    private String maskedCardNumber;


    public Receipt(int userId, int ticketId, String cardNumber, LocalDateTime paymentDate, double totalAmount) {
        this.userId = userId;
        this.ticketId = ticketId;
        this.cardNumber = cardNumber;
        this.paymentDate = paymentDate;
        this.totalAmount = totalAmount;
        this.maskedCardNumber = maskCardNumber(cardNumber);
    }

    // ctor for retrieving receipt details
    public Receipt(int paymentId, int userId, int ticketId, String cardNumber, LocalDateTime paymentDate, double totalAmount, String maskedCardNumber) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.ticketId = ticketId;
        this.cardNumber = cardNumber;
        this.paymentDate = paymentDate;
        this.totalAmount = totalAmount;
        this.maskedCardNumber = maskedCardNumber;
    }

    public boolean saveToDatabase() {
        String query = "INSERT INTO Receipt (user_id, ticket_id, card_number, payment_date, total_amount, masked_card_number) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, ticketId);
            preparedStatement.setString(3, cardNumber);
            preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(paymentDate));
            preparedStatement.setDouble(5, totalAmount);
            preparedStatement.setString(6, maskedCardNumber);

            int rowsAffected = preparedStatement.executeUpdate();

            // Retrieve the generated receipt ID
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.paymentId = generatedKeys.getInt(1);
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
        }
        return false;
    }


    private String maskCardNumber(String cardNumber) {
        if (cardNumber != null && cardNumber.length() == 16) {
            return "****-****-****-" + cardNumber.substring(12);
        }
        return null;
    }

    public static Receipt fetchPaymentById(int paymentId) {
        String query = "SELECT * FROM Receipt WHERE payment_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, paymentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Receipt(
                        resultSet.getInt("payment_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("ticket_id"),
                        resultSet.getString("card_number"),
                        resultSet.getTimestamp("payment_date").toLocalDateTime(),
                        resultSet.getDouble("total_amount"),
                        resultSet.getString("masked_card_number")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching payment: " + e.getMessage());
        }
        return null;
    }

    // Getters
    public int getPaymentId() {
        return paymentId;
    }

    public int getUserId() {
        return userId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }
}

