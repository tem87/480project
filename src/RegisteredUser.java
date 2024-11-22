import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisteredUser extends User {
    private boolean annualFeePaid;

    // Constructor for creating a new Registered User
    public RegisteredUser(String name, String email, String password, String phoneNumber, String address) {
        super(name, email, password, phoneNumber, address);
        this.annualFeePaid = false;
    }

    // Constructor for existing Registered Users
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

    // Save registered user to the database
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

    // Authenticate registered user
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

        return null; // Authentication failed
    }
}
