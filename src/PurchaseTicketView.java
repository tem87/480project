import javax.swing.*;
import java.awt.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PurchaseTicketView {

    public static void showPurchaseTicketView(JFrame frame, Runnable backToMenuCallback) {
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

        // Populate Theatre Dropdown
        List<Theatre> theatres = Theatre.fetchTheaters();
        for (Theatre theatre : theatres) {
            theatreDropdown.addItem(theatre.getName() + " - " + theatre.getLocation());
        }

        theatreDropdown.addActionListener(e -> {
            movieDropdown.removeAllItems();
            showtimeDropdown.removeAllItems();

            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            if (selectedTheatreIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                List<Movie> movies = Movie.fetchMovies();

                for (Movie movie : movies) {
                    movieDropdown.addItem(movie.getTitle());
                }
            }
        });

        movieDropdown.addActionListener(e -> {
            showtimeDropdown.removeAllItems();

            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            int selectedMovieIndex = movieDropdown.getSelectedIndex();

            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                Movie selectedMovie = Movie.fetchMovies().get(selectedMovieIndex);

                List<Showtime> showtimes = Showtime.fetchShowtimes();
                for (Showtime showtime : showtimes) {
                    if (showtime.getMovieID() == selectedMovie.getMovieID()
                            && showtime.getTheaterID() == selectedTheatre.getTheatreID()) {
                        showtimeDropdown.addItem(showtime.getDateTime().toString());
                    }
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

        seatMapButton.addActionListener(e -> {
            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            int selectedMovieIndex = movieDropdown.getSelectedIndex();
            int selectedShowtimeIndex = showtimeDropdown.getSelectedIndex();

            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0 && selectedShowtimeIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                Movie selectedMovie = Movie.fetchMovies().get(selectedMovieIndex);
                Showtime selectedShowtime = Showtime.fetchShowtimes().get(selectedShowtimeIndex);

                // Open SeatSelectionView
                SeatSelectionView.showSeatMap(frame, selectedShowtime.getShowtimeID(), backToMenuCallback, selectedTheatre, selectedMovie, selectedShowtime);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a theatre, movie, and showtime to proceed.");
            }
        });

        backButton.addActionListener(e -> backToMenuCallback.run());

        panel.add(seatMapButton);
        panel.add(backButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
}