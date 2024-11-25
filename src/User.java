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

    public User(String name, String email, String password, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isRegistered = false;
    }

    public User(int userId, String name, String email, String password, String phoneNumber, String address, boolean isRegistered) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isRegistered = isRegistered;
    }

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

    public boolean saveToDatabase() {
        String query = "INSERT INTO Users (name, email, password, phone_number, address, is_registered, annual_fee_paid) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, phoneNumber);
            preparedStatement.setString(5, address);
            preparedStatement.setBoolean(6, isRegistered);
            preparedStatement.setBoolean(7, false);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateInformation(String newName, String newEmail, String newPassword) {
        StringBuilder query = new StringBuilder("UPDATE Users SET");
        boolean updateName = newName != null && !newName.equals(this.name);
        boolean updateEmail = newEmail != null && !newEmail.equals(this.email);
        boolean updatePassword = newPassword != null && !newPassword.equals(this.password);

        if (updateName) query.append(" name = ?,");
        if (updateEmail) query.append(" email = ?,");
        if (updatePassword) query.append(" password = ?,");
        query.deleteCharAt(query.length() - 1);
        query.append(" WHERE user_id = ?");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (updateName) preparedStatement.setString(paramIndex++, newName);
            if (updateEmail) preparedStatement.setString(paramIndex++, newEmail);
            if (updatePassword) preparedStatement.setString(paramIndex++, newPassword);
            preparedStatement.setInt(paramIndex, this.userId);

            if (preparedStatement.executeUpdate() > 0) {
                if (updateName) this.name = newName;
                if (updateEmail) this.email = newEmail;
                if (updatePassword) this.password = newPassword;
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user information: " + e.getMessage());
        }
        return false;
    }

    public static int getUserIdByEmail(String email) {
        String query = "SELECT user_id FROM Users WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user ID by email: " + e.getMessage());
        }
        return -1;
    }

    public String fetchTicketsFromDatabase() {
        StringBuilder tickets = new StringBuilder();
        String query = "SELECT T.ticket_id, M.title AS movie_title, S.start_time AS showtime_time, Seats.seat_number, T.price, T.status, T.purchase_date FROM Tickets T JOIN Showtime S ON T.showtime_id = S.showtime_id JOIN Movie M ON S.movie_id = M.movie_id JOIN Seats ON T.seat_id = Seats.seat_id WHERE T.user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, this.userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tickets.append("Ticket ID: ").append(resultSet.getInt("ticket_id"))
                        .append(", Movie: ").append(resultSet.getString("movie_title"))
                        .append(", Showtime: ").append(resultSet.getTimestamp("showtime_time"))
                        .append(", Seat: ").append(resultSet.getString("seat_number"))
                        .append(", Price: $").append(resultSet.getBigDecimal("price"))
                        .append(", Status: ").append(resultSet.getString("status"))
                        .append(", Purchase Date: ").append(resultSet.getTimestamp("purchase_date"))
                        .append("\n");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tickets: " + e.getMessage());
        }
        return tickets.toString().isEmpty() ? "No tickets found for this user." : tickets.toString();
    }

    public static User fetchByEmail(String email) {
        String query = "SELECT * FROM Users WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                boolean isRegistered = resultSet.getBoolean("is_registered");
                if (isRegistered) {
                    return new RegisteredUser(resultSet.getInt("user_id"), resultSet.getString("name"), resultSet.getString("email"), resultSet.getString("password"), resultSet.getString("phone_number"), resultSet.getString("address"), resultSet.getBoolean("annual_fee_paid"));
                } else {
                    return new User(resultSet.getInt("user_id"), resultSet.getString("name"), resultSet.getString("email"), resultSet.getString("password"), resultSet.getString("phone_number"), resultSet.getString("address"), isRegistered);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public static int saveGuestUserToDatabase(String name, String email) {
        String query = "INSERT INTO Users (name, email, password, is_registered, annual_fee_paid) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, "guest");
            stmt.setBoolean(4, false);
            stmt.setBoolean(5, false);
            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving guest user: " + e.getMessage());
        }
        return -1;
    }
}