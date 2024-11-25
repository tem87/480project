import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseTicketViewRU {

    public static void showPurchaseTicketView(JFrame frame, RegisteredUser loggedInUser, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        JLabel titleLabel = new JLabel("Purchase Ticket", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel);

        // Dropdowns for Theatre, Movie, and Showtime
        JComboBox<String> theatreDropdown = new JComboBox<>();
        JComboBox<String> movieDropdown = new JComboBox<>();
        JComboBox<String> showtimeDropdown = new JComboBox<>();

        // Fetch all theatres and populate dropdown
        List<Theatre> theatres = Theatre.fetchTheaters();
        List<Movie> filteredMovies = new ArrayList<>(); // Holds filtered movies for the selected theatre
        List<Showtime> filteredShowtimes = new ArrayList<>(); // Holds filtered showtimes for the selected theatre and movie

        for (Theatre theatre : theatres) {
            theatreDropdown.addItem(theatre.getName() + " - " + theatre.getLocation());
        }

        // Theatre selection action
        theatreDropdown.addActionListener(e -> {
            movieDropdown.removeAllItems();
            showtimeDropdown.removeAllItems();

            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            if (selectedTheatreIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);

                // Filter movies based on the selected theatre
                filteredMovies.clear();
                for (Movie movie : Movie.fetchMovies()) {
                    boolean isMovieAtTheatre = Showtime.fetchShowtimes().stream()
                            .anyMatch(showtime -> showtime.getTheaterID() == selectedTheatre.getTheatreID()
                                    && showtime.getMovieID() == movie.getMovieID());
                    if (isMovieAtTheatre) {
                        filteredMovies.add(movie);
                        movieDropdown.addItem(movie.getTitle());
                    }
                }

                if (filteredMovies.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No movies available at the selected theater.");
                }
            }
        });

        // Movie selection action
        movieDropdown.addActionListener(e -> {
            showtimeDropdown.removeAllItems();

            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            int selectedMovieIndex = movieDropdown.getSelectedIndex();

            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                Movie selectedMovie = filteredMovies.get(selectedMovieIndex);

                // Filter showtimes based on the selected theatre and movie
                filteredShowtimes.clear();
                for (Showtime showtime : Showtime.fetchShowtimes()) {
                    if (showtime.getTheaterID() == selectedTheatre.getTheatreID() &&
                            showtime.getMovieID() == selectedMovie.getMovieID()) {
                        filteredShowtimes.add(showtime);
                        showtimeDropdown.addItem(showtime.getDateTime().toString());
                    }
                }

                if (filteredShowtimes.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No showtimes available for the selected movie at the selected theater.");
                }
            }
        });

        panel.add(new JLabel("Select Theatre:", SwingConstants.CENTER));
        panel.add(theatreDropdown);

        panel.add(new JLabel("Select Movie:", SwingConstants.CENTER));
        panel.add(movieDropdown);

        panel.add(new JLabel("Select Showtime:", SwingConstants.CENTER));
        panel.add(showtimeDropdown);

        // Buttons
        JButton seatMapButton = new JButton("View Seat Map");
        JButton backButton = new JButton("Back to Menu");

        // View seat map button action
        seatMapButton.addActionListener(e -> {
            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            int selectedMovieIndex = movieDropdown.getSelectedIndex();
            int selectedShowtimeIndex = showtimeDropdown.getSelectedIndex();

            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0 && selectedShowtimeIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                Movie selectedMovie = filteredMovies.get(selectedMovieIndex);
                Showtime selectedShowtime = filteredShowtimes.get(selectedShowtimeIndex);

                // Display seat map view
                SeatSelectionView.showSeatMap(
                        frame,
                        selectedShowtime.getShowtimeID(),
                        backToMenuCallback,
                        selectedTheatre,
                        selectedMovie,
                        selectedShowtime,
                        loggedInUser
                );
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a theatre, movie, and showtime to proceed.");
            }
        });

        // Back button action
        backButton.addActionListener(e -> backToMenuCallback.run());

        panel.add(seatMapButton);
        panel.add(backButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
}