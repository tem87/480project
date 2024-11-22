import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovieView {

    // Show movies and provide a dynamic back button callback
    public static void showMovie(JFrame frame, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        List<Movie> movies = Movie.fetchMovies();

        String[] columnNames = {"ID", "Title", "Genre", "Rating", "Synopsis", "Length"};

        Object[][] data = new Object[movies.size()][columnNames.length];
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            data[i][0] = movie.getMovieID();
            data[i][1] = movie.getTitle();
            data[i][2] = movie.getGenre();
            data[i][3] = movie.getRating();
            data[i][4] = movie.getSynopsis();
            data[i][5] = movie.getLength();
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(table);

        // Back button to dynamically return to the user's menu
        JButton backButton = new JButton("Back to Menu");
        backButton.setBackground(Color.DARK_GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> backToMenuCallback.run());

        JPanel moviesPanel = new JPanel(new BorderLayout());
        moviesPanel.add(scrollPane, BorderLayout.CENTER);
        moviesPanel.add(backButton, BorderLayout.SOUTH);

        frame.add(moviesPanel, BorderLayout.CENTER);
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

        Object[] message = {
                "Title:", titleField,
                "Genre:", genreField,
                "Rating:", ratingField,
                "Synopsis:", synopsisField,
                "Length:", lengthField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add New Movie", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String genre = genreField.getText();
            String rating = ratingField.getText();
            String synopsis = synopsisField.getText();
            String length = lengthField.getText();

            Movie newMovie = new Movie(title, genre, rating, synopsis, length);
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

                Object[] message = {
                        "Title:", titleField,
                        "Genre:", genreField,
                        "Rating:", ratingField,
                        "Synopsis:", synopsisField,
                        "Length:", lengthField
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "Modify Movie", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    movieToEdit = new Movie(
                            movieToEdit.getMovieID(),
                            titleField.getText(),
                            genreField.getText(),
                            ratingField.getText(),
                            synopsisField.getText(),
                            lengthField.getText()
                    );

                    if (movieToEdit.modifyMovie()) {
                        JOptionPane.showMessageDialog(frame, "Movie updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to update movie.");
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
                    if (Movie.deleteMovie(movieToDelete.getMovieID())) {
                        JOptionPane.showMessageDialog(frame, "Movie deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete movie.");
                    }
                }
            }
        }
    }
}
