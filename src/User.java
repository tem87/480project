import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private boolean isRegistered;
    private boolean annualFeePaid;

    // Constructor for creating new users (no ID)
    public User(String name, String email, String password, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isRegistered = true;
        this.annualFeePaid = false;
    }

    // Constructor for existing users (with ID)
    public User(int userId, String name, String email, String password, String phoneNumber, String address,
                boolean isRegistered, boolean annualFeePaid) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isRegistered = isRegistered;
        this.annualFeePaid = annualFeePaid;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public boolean isAnnualFeePaid() {
        return annualFeePaid;
    }

    // Save user to the database
    public boolean saveToDatabase() {
        String query = "INSERT INTO Users (name, email, password, phone_number, address, is_registered, annual_fee_paid) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, phoneNumber);
            preparedStatement.setString(5, address);
            preparedStatement.setBoolean(6, isRegistered);
            preparedStatement.setBoolean(7, annualFeePaid);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    // Fetch user from the database by email
    public static User fetchByEmail(String email) {
        String query = "SELECT * FROM Users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("address"),
                        resultSet.getBoolean("is_registered"),
                        resultSet.getBoolean("annual_fee_paid")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }

        return null; // User not found
    }

    public static User authenticate(String email, String password) {
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("address"),
                        resultSet.getBoolean("is_registered"),
                        resultSet.getBoolean("annual_fee_paid")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }

        return null; // Authentication failed
    }

}
