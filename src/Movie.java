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
    //private String imagePath;

    // Constructor for retrieving existing movies
    public Movie(int movieID, String title, String genre, String rating, String synopsis, String length) {
        this.movieID = movieID;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.synopsis = synopsis;
        this.length = length;
        //this.imagePath = imagePath;
    }

    // Constructor for creating new movies (without ID)
    public Movie(String title, String genre, String rating, String synopsis, String length) {
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.synopsis = synopsis;
        this.length = length;
    }

    // Getters
    public int getMovieID() {
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

    public boolean addMovie() {
        String query = "INSERT INTO Movie (title, genre, rating, synopsis, length) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, genre);
            preparedStatement.setString(3, rating);
            preparedStatement.setString(4, synopsis);
            preparedStatement.setString(5, length);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
            return false;
        }
    }

    public boolean modifyMovie() {
        String query = "UPDATE Movie SET title = ?, genre = ?, rating = ?, synopsis = ?, length = ? WHERE movie_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, genre);
            preparedStatement.setString(3, rating);
            preparedStatement.setString(4, synopsis);
            preparedStatement.setString(5, length);
            preparedStatement.setInt(6, movieID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating movie: " + e.getMessage());
            return false;
        }
    }

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

    // Fetch movies from the database
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

                movies.add(new Movie(movieID, title, genre, rating, synopsis, length));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching movies: " + e.getMessage());
        }

        return movies;
    }
    public String toTableString() {
        return String.format("%-5d %-30s %-15s %-10s %-50s %-10s",
                movieID, title, genre, rating, synopsis, length);
    }

    @Override
    public String toString() {
        return String.format("Movie{movieID=%d, title='%s', genre='%s', rating='%s', synopsis='%s', length='%s'}",
                movieID, title, genre, rating, synopsis, length);
    }
}
