import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovieView {

    // Show all movies
    // Show all movies for admin view
    public static void showMoviesAdminView(JFrame frame, Runnable backToMenuCallback) {
        List<Movie> movies = Movie.fetchMovies();
        String[] columnNames = {"ID", "Title", "Genre", "Rating", "Synopsis", "Length", "Price", "Early Access"};
        Object[][] data = new Object[movies.size()][columnNames.length];

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            data[i][0] = movie.getMovieId();
            data[i][1] = movie.getTitle();
            data[i][2] = movie.getGenre();
            data[i][3] = movie.getRating();
            data[i][4] = movie.getSynopsis();
            data[i][5] = movie.getLength();
            data[i][6] = String.format("$%.2f", movie.getPrice());
            data[i][7] = movie.isEarlyAccess() ? "Yes" : "No";
        }

        displayTable(frame, "All Movies", columnNames, data, backToMenuCallback);
    }

    // Show movies that are NOT early access
    public static void showMovie(JFrame frame, Runnable backToMenuCallback) {
        List<Movie> movies = Movie.fetchMoviesNoEarlyAccess();
        String[] columnNames = {"Title", "Genre", "Rating", "Synopsis", "Length", "Price"};
        Object[][] data = new Object[movies.size()][columnNames.length];

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            data[i][0] = movie.getTitle();
            data[i][1] = movie.getGenre();
            data[i][2] = movie.getRating();
            data[i][3] = movie.getSynopsis();
            data[i][4] = movie.getLength();
            data[i][5] = String.format("$%.2f", movie.getPrice());
        }

        displayTable(frame, "Available Movies", columnNames, data, backToMenuCallback);
    }

    // Show early access movies
    public static void showEarlyAccessMovies(JFrame frame, Runnable backToMenuCallback) {
        List<Movie> movies = Movie.fetchEarlyAccessMovies();
        String[] columnNames = {"Title", "Genre", "Rating", "Synopsis", "Length", "Price"};
        Object[][] data = new Object[movies.size()][columnNames.length];

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            data[i][0] = movie.getTitle();
            data[i][1] = movie.getGenre();
            data[i][2] = movie.getRating();
            data[i][3] = movie.getSynopsis();
            data[i][4] = movie.getLength();
            data[i][5] = String.format("$%.2f", movie.getPrice());
        }

        displayTable(frame, "Early Access Movies", columnNames, data, backToMenuCallback);
    }

    // Helper method to display tables
    private static void displayTable(JFrame frame, String title, String[] columnNames, Object[][] data, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JTable table = VisualGui.createStyledTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        JLabel titleLabel = VisualGui.createStyledTitle(title);
        JButton backButton = VisualGui.createStyledButton("Back to Menu", backToMenuCallback);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // Add a new movie (Admin-specific functionality)
    public static void addMovie(JFrame frame) {
        JTextField titleField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField ratingField = new JTextField();
        JTextField synopsisField = new JTextField();
        JTextField lengthField = new JTextField();
        JTextField priceField = new JTextField();
        JCheckBox earlyAccessCheckbox = new JCheckBox();

        Object[] message = {
                "Title:", titleField,
                "Genre:", genreField,
                "Rating:", ratingField,
                "Synopsis:", synopsisField,
                "Length:", lengthField,
                "Price:", priceField,
                "Early Access:", earlyAccessCheckbox
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add New Movie", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (titleField.getText().isEmpty() || genreField.getText().isEmpty() ||
                    ratingField.getText().isEmpty() || synopsisField.getText().isEmpty() ||
                    lengthField.getText().isEmpty() || priceField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String title = titleField.getText();
            String genre = genreField.getText();
            String rating = ratingField.getText();
            String synopsis = synopsisField.getText();
            String length = lengthField.getText();
            boolean earlyAccess = earlyAccessCheckbox.isSelected();
            double price;

            try {
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid price. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Movie newMovie = new Movie(title, genre, rating, synopsis, length, price, earlyAccess);
            if (newMovie.addMovie()) {
                JOptionPane.showMessageDialog(frame, "Movie added successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add movie.");
            }
        }
    }

    // Modify an existing movie (Admin-specific functionality)
    public static void modifyMovie(JFrame frame) {
        List<Movie> movies = Movie.fetchMovies();
        String[] movieTitles = movies.stream().map(Movie::getTitle).toArray(String[]::new);

        String selectedMovie = (String) JOptionPane.showInputDialog(
                frame,
                "Select a movie to modify:",
                "Modify Movie",
                JOptionPane.PLAIN_MESSAGE,
                null,
                movieTitles,
                null
        );

        if (selectedMovie != null) {
            Movie movieToEdit = movies.stream()
                    .filter(movie -> movie.getTitle().equals(selectedMovie))
                    .findFirst()
                    .orElse(null);

            if (movieToEdit != null) {
                JTextField titleField = new JTextField(movieToEdit.getTitle());
                JTextField genreField = new JTextField(movieToEdit.getGenre());
                JTextField ratingField = new JTextField(movieToEdit.getRating());
                JTextField synopsisField = new JTextField(movieToEdit.getSynopsis());
                JTextField lengthField = new JTextField(movieToEdit.getLength());
                JTextField priceField = new JTextField(String.valueOf(movieToEdit.getPrice()));
                JCheckBox earlyAccessCheckbox = new JCheckBox();
                earlyAccessCheckbox.setSelected(movieToEdit.isEarlyAccess());

                Object[] message = {
                        "Title:", titleField,
                        "Genre:", genreField,
                        "Rating:", ratingField,
                        "Synopsis:", synopsisField,
                        "Length:", lengthField,
                        "Price:", priceField,
                        "Early Access:", earlyAccessCheckbox
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "Modify Movie", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    if (titleField.getText().isEmpty() || genreField.getText().isEmpty() ||
                            ratingField.getText().isEmpty() || synopsisField.getText().isEmpty() ||
                            lengthField.getText().isEmpty() || priceField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceField.getText());
                        movieToEdit = new Movie(
                                movieToEdit.getMovieId(),
                                titleField.getText(),
                                genreField.getText(),
                                ratingField.getText(),
                                synopsisField.getText(),
                                lengthField.getText(),
                                price,
                                earlyAccessCheckbox.isSelected()
                        );

                        if (movieToEdit.modifyMovie()) {
                            JOptionPane.showMessageDialog(frame, "Movie updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Failed to update movie.");
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(frame, "Invalid price. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    // Delete an existing movie (Admin-specific functionality)
    public static void deleteMovie(JFrame frame) {
        List<Movie> movies = Movie.fetchMovies();
        String[] movieTitles = movies.stream().map(Movie::getTitle).toArray(String[]::new);

        String selectedMovie = (String) JOptionPane.showInputDialog(
                frame,
                "Select a movie to delete:",
                "Delete Movie",
                JOptionPane.PLAIN_MESSAGE,
                null,
                movieTitles,
                null
        );

        if (selectedMovie != null) {
            Movie movieToDelete = movies.stream()
                    .filter(movie -> movie.getTitle().equals(selectedMovie))
                    .findFirst()
                    .orElse(null);

            if (movieToDelete != null) {
                int confirmation = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to delete " + selectedMovie + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmation == JOptionPane.YES_OPTION) {
                    if (Movie.deleteMovie(movieToDelete.getMovieId())) {
                        JOptionPane.showMessageDialog(frame, "Movie deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete movie.");
                    }
                }
            }
        }
    }
}
