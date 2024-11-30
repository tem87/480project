import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int movieID;
    private String title;
    private String genre;
    private String rating;
    private String synopsis;
    private String length;
    private double price;
    private boolean earlyAccess;

    // Constructor for retrieving existing movies
    public Movie(int movieID, String title, String genre, String rating, String synopsis, String length, double price, boolean earlyAccess) {
        this.movieID = movieID;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.synopsis = synopsis;
        this.length = length;
        this.price = price;
        this.earlyAccess = earlyAccess;
    }

    // Constructor for creating new movies (without ID)
    public Movie(String title, String genre, String rating, String synopsis, String length, double price, boolean earlyAccess) {
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.synopsis = synopsis;
        this.length = length;
        this.price = price;
        this.earlyAccess = earlyAccess;
    }

    // Getters
    public int getMovieId() {
        return movieID;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getRating() {
        return rating;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getLength() {
        return length;
    }

    public double getPrice() {
        return price;
    }

    public boolean isEarlyAccess() {
        return earlyAccess;
    }

    // Add movie to the database
    public boolean addMovie() {
        String query = "INSERT INTO Movie (title, genre, rating, synopsis, length, price, early_access) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, genre);
            preparedStatement.setString(3, rating);
            preparedStatement.setString(4, synopsis);
            preparedStatement.setString(5, length);
            preparedStatement.setDouble(6, price);
            preparedStatement.setBoolean(7, earlyAccess);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
            return false;
        }
    }

    // Update an existing movie
    public boolean modifyMovie() {
        String query = "UPDATE Movie SET title = ?, genre = ?, rating = ?, synopsis = ?, length = ?, price = ?, early_access = ? WHERE movie_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, genre);
            preparedStatement.setString(3, rating);
            preparedStatement.setString(4, synopsis);
            preparedStatement.setString(5, length);
            preparedStatement.setDouble(6, price);
            preparedStatement.setBoolean(7, earlyAccess);
            preparedStatement.setInt(8, movieID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating movie: " + e.getMessage());
            return false;
        }
    }

    // Delete a movie from the database
    public static boolean deleteMovie(int movieID) {
        String query = "DELETE FROM Movie WHERE movie_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, movieID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting movie: " + e.getMessage());
            return false;
        }
    }

    // Fetch all movies from the database
    public static List<Movie> fetchMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM Movie";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int movieID = resultSet.getInt("movie_id");
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                String rating = resultSet.getString("rating");
                String synopsis = resultSet.getString("synopsis");
                String length = resultSet.getString("length");
                double price = resultSet.getDouble("price");
                boolean earlyAccess = resultSet.getBoolean("early_access");

                movies.add(new Movie(movieID, title, genre, rating, synopsis, length, price, earlyAccess));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching movies: " + e.getMessage());
        }

        return movies;
    }

    // Fetch movies without early access
    public static List<Movie> fetchMoviesNoEarlyAccess() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM Movie WHERE early_access = FALSE";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int movieID = resultSet.getInt("movie_id");
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                String rating = resultSet.getString("rating");
                String synopsis = resultSet.getString("synopsis");
                String length = resultSet.getString("length");
                double price = resultSet.getDouble("price");
                boolean earlyAccess = resultSet.getBoolean("early_access");

                movies.add(new Movie(movieID, title, genre, rating, synopsis, length, price, earlyAccess));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching non-early access movies: " + e.getMessage());
        }

        return movies;
    }


    // Fetch only movies with early access
    public static List<Movie> fetchEarlyAccessMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM Movie WHERE early_access = TRUE";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int movieID = resultSet.getInt("movie_id");
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                String rating = resultSet.getString("rating");
                String synopsis = resultSet.getString("synopsis");
                String length = resultSet.getString("length");
                double price = resultSet.getDouble("price");
                boolean earlyAccess = resultSet.getBoolean("early_access");

                movies.add(new Movie(movieID, title, genre, rating, synopsis, length, price, earlyAccess));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching early access movies: " + e.getMessage());
        }

        return movies;
    }

    @Override
    public String toString() {
        return String.format("Movie{movieID=%d, title='%s', genre='%s', rating='%s', synopsis='%s', length='%s', price=%.2f, earlyAccess=%b}",
                movieID, title, genre, rating, synopsis, length, price, earlyAccess);
    }
}
