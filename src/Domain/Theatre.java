package Domain;

import Database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Theatre {
    private int theaterID;
    private String name;
    private String location;

    // ctor for existing theaters
    public Theatre(int theaterID, String name, String location) {
        this.theaterID = theaterID;
        this.name = name;
        this.location = location;
    }

    // ctor for new theaters
    public Theatre(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // getters
    public int getTheatreID() {
        return theaterID;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public static List<Theatre> fetchTheaters() {
        List<Theatre> theaters = new ArrayList<>();
        String query = "SELECT * FROM Theater";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int theaterID = resultSet.getInt("theater_id");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");

                theaters.add(new Theatre(theaterID, name, location));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching theaters: " + e.getMessage());
        }

        return theaters;
    }

    public boolean addTheatre() {
        String query = "INSERT INTO Theater (name, location) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, location);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting theater: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTheater() {
        String query = "UPDATE Theater SET name = ?, location = ? WHERE theater_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, location);
            preparedStatement.setInt(3, theaterID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating theater: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String toString() {
        return "Theater{" +
                "theaterID=" + theaterID +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
