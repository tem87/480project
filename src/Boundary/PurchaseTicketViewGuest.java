package Boundary;
import Visual.*;
import Domain.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseTicketViewGuest {

    public static void showPurchaseTicketView(JFrame frame, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        JLabel titleLabel = new JLabel("Purchase Ticket", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        panel.add(titleLabel);

        JComboBox<String> theatreDropdown = new JComboBox<>();
        JComboBox<String> movieDropdown = new JComboBox<>();
        JComboBox<String> showtimeDropdown = new JComboBox<>();

        // fetch data from database
        List<Theatre> theatres = Theatre.fetchTheaters();
        List<Movie> allMovies = Movie.fetchMoviesNoEarlyAccess();
        List<Showtime> allShowtimes = Showtime.fetchShowtimes();

        List<Movie> filteredMovies = new ArrayList<>();
        List<Showtime> filteredShowtimes = new ArrayList<>();


        for (Theatre theatre : theatres) {
            theatreDropdown.addItem(theatre.getName() + " - " + theatre.getLocation());
        }


        theatreDropdown.addActionListener(e -> {
            movieDropdown.removeAllItems();
            showtimeDropdown.removeAllItems();

            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            if (selectedTheatreIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                filteredMovies.clear();
                for (Movie movie : allMovies) {
                    boolean isMovieAvailable = allShowtimes.stream()
                            .anyMatch(showtime -> showtime.getTheaterID() == selectedTheatre.getTheatreID()
                                    && showtime.getMovieID() == movie.getMovieId());
                    if (isMovieAvailable) {
                        filteredMovies.add(movie);
                        movieDropdown.addItem(movie.getTitle());
                    }
                }

                if (filteredMovies.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No movies available at the selected theater.");
                }
            }
        });

        movieDropdown.addActionListener(e -> {
            showtimeDropdown.removeAllItems();

            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            int selectedMovieIndex = movieDropdown.getSelectedIndex();

            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                Movie selectedMovie = filteredMovies.get(selectedMovieIndex);

                // Filter showtimes based on the selected theatre and movie
                filteredShowtimes.clear();
                for (Showtime showtime : allShowtimes) {
                    if (showtime.getTheaterID() == selectedTheatre.getTheatreID()
                            && showtime.getMovieID() == selectedMovie.getMovieId()) {
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

        //JButton seatMapButton = new JButton("View Seat Map");
        JButton seatMapButton = VisualGui.createStyledButtonSmall("View Seat Map");
        //JButton backButton = new JButton("Back to Menu");
        JButton backButton = VisualGui.createStyledButtonSmall("Back to Menu");

        seatMapButton.addActionListener(e -> {
            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            int selectedMovieIndex = movieDropdown.getSelectedIndex();
            int selectedShowtimeIndex = showtimeDropdown.getSelectedIndex();

            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0 && selectedShowtimeIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                Movie selectedMovie = filteredMovies.get(selectedMovieIndex);
                Showtime selectedShowtime = filteredShowtimes.get(selectedShowtimeIndex);

                SeatSelectionView.showSeatMap(frame, selectedShowtime.getShowtimeID(), backToMenuCallback,
                        selectedTheatre, selectedMovie, selectedShowtime, null);
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
        VisualGui.applyGlobalFont(frame.getContentPane(), VisualGui.fontTable);
    }
}