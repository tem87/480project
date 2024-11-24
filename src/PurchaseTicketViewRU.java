import javax.swing.*;
import java.awt.*;
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
        for (Theatre theatre : theatres) {
            theatreDropdown.addItem(theatre.getName() + " - " + theatre.getLocation());
        }

        theatreDropdown.addActionListener(e -> {
            movieDropdown.removeAllItems();
            showtimeDropdown.removeAllItems();

            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            if (selectedTheatreIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                List<Movie> allMovies = Movie.fetchMovies();

                // Populate movie dropdown with movies at the selected theatre
                for (Movie movie : allMovies) {
                    boolean isMovieAtTheatre = false;
                    for (Showtime showtime : Showtime.fetchShowtimes()) {
                        if (showtime.getTheaterID() == selectedTheatre.getTheatreID() &&
                                showtime.getMovieID() == movie.getMovieID()) {
                            isMovieAtTheatre = true;
                            break;
                        }
                    }
                    if (isMovieAtTheatre) {
                        movieDropdown.addItem(movie.getTitle());
                    }
                }

                if (movieDropdown.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(frame, "No movies available at the selected theater.");
                }
            }
        });

//        movieDropdown.addActionListener(e -> {
//            showtimeDropdown.removeAllItems();
//
//            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
//            int selectedMovieIndex = movieDropdown.getSelectedIndex();
//
//            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0) {
//                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
//                Movie selectedMovie = Movie.fetchMovies().get(selectedMovieIndex);
//
//                // Populate showtime dropdown with showtimes matching selected theatre and movie
//                for (Showtime showtime : Showtime.fetchShowtimes()) {
//                    if (showtime.getTheaterID() == selectedTheatre.getTheatreID() &&
//                            showtime.getMovieID() == selectedMovie.getMovieID()) {
//                        showtimeDropdown.addItem(showtime.getDateTime().toString());
//                    }
//                }
//
//                if (showtimeDropdown.getItemCount() == 0) {
//                    JOptionPane.showMessageDialog(frame, "No showtimes available for the selected movie at the selected theater.");
//                }
//            }
//        });
        movieDropdown.addActionListener(e -> {
            showtimeDropdown.removeAllItems();

            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            int selectedMovieIndex = movieDropdown.getSelectedIndex();

            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                Movie selectedMovie = Movie.fetchMovies().get(selectedMovieIndex);

                // Populate showtime dropdown
                List<Showtime> showtimes = Showtime.fetchShowtimes();
                for (Showtime showtime : showtimes) {
                    if (showtime.getTheaterID() == selectedTheatre.getTheatreID() &&
                            showtime.getMovieID() == selectedMovie.getMovieID()) {
                        showtimeDropdown.addItem(showtime.getDateTime().toString());
                    }
                }

                if (showtimeDropdown.getItemCount() == 0) {
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

        seatMapButton.addActionListener(e -> {
            int selectedTheatreIndex = theatreDropdown.getSelectedIndex();
            int selectedMovieIndex = movieDropdown.getSelectedIndex();
            int selectedShowtimeIndex = showtimeDropdown.getSelectedIndex();

            if (selectedTheatreIndex >= 0 && selectedMovieIndex >= 0 && selectedShowtimeIndex >= 0) {
                Theatre selectedTheatre = theatres.get(selectedTheatreIndex);
                Movie selectedMovie = Movie.fetchMovies().get(selectedMovieIndex);
                Showtime selectedShowtime = Showtime.fetchShowtimes().get(selectedShowtimeIndex);

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

        backButton.addActionListener(e -> backToMenuCallback.run());

        panel.add(seatMapButton);
        panel.add(backButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
}