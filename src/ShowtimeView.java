import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeView {

    public static void showShowtime(JFrame frame, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Fetching showtime data
        List<Showtime> showtimes = Showtime.fetchShowtimes();
        String[] columnNames = {"ID", "Movie ID", "Theater ID", "Start Time", "Max Seats"};

        Object[][] data = new Object[showtimes.size()][columnNames.length];
        for (int i = 0; i < showtimes.size(); i++) {
            Showtime showtime = showtimes.get(i);
            data[i][0] = showtime.getShowtimeID();
            data[i][1] = showtime.getMovieID();
            data[i][2] = showtime.getTheaterID();
            data[i][3] = showtime.getDateTime();
            data[i][4] = showtime.getMaxSeats();
        }

        // Styled JTable
        JTable table = VisualGui.createStyledTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // Styled back button
        JButton backButton = VisualGui.createStyledButton("Back to Menu", backToMenuCallback);

        // Layout panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public static void showShowtimeDetails(JFrame frame, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Fetching guest showtime data
        List<Showtime> showtimes = Showtime.fetchShowtimesGuest();
        String[] columnNames = {"Movie Name", "Theater Name", "Start Time"};

        Object[][] data = new Object[showtimes.size()][columnNames.length];
        for (int i = 0; i < showtimes.size(); i++) {
            Showtime showtime = showtimes.get(i);
            data[i][0] = showtime.getMovieName();
            data[i][1] = showtime.getTheaterName();
            data[i][2] = showtime.getDateTime();
        }

        // Styled JTable
        JTable table = VisualGui.createStyledTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // Styled back button
        JButton backButton = VisualGui.createStyledButton("Back to Menu", backToMenuCallback);

        // Layout panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }





    public static void addShowtime(JFrame frame) {
        JTextField movieIDField = new JTextField();
        JTextField theaterIDField = new JTextField();
        JTextField dateTimeField = new JTextField();
        JTextField maxSeatsField = new JTextField();

        Object[] message = {
                "Movie ID:", movieIDField,
                "Theater ID:", theaterIDField,
                "Start Time (YYYY-MM-DD HH:MM:SS):", dateTimeField,
                "Max Seats:", maxSeatsField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add New Showtime", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                // Trim input and validate each field
                String movieIDText = movieIDField.getText().trim();
                String theaterIDText = theaterIDField.getText().trim();
                String dateTimeText = dateTimeField.getText().trim();
                String maxSeatsText = maxSeatsField.getText().trim();

                if (movieIDText.isEmpty() || theaterIDText.isEmpty() || dateTimeText.isEmpty() || maxSeatsText.isEmpty()) {
                    throw new IllegalArgumentException("All fields must be filled!");
                }

                int movieID = Integer.parseInt(movieIDText);
                int theaterID = Integer.parseInt(theaterIDText);
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                int maxSeats = Integer.parseInt(maxSeatsText);

                if (maxSeats <= 0) {
                    throw new IllegalArgumentException("Max Seats must be greater than 0!");
                }

                Showtime newShowtime = new Showtime(movieID, theaterID, dateTime, maxSeats);
                if (newShowtime.addShowtime()) {
                    JOptionPane.showMessageDialog(frame, "Showtime added successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to add showtime.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Movie ID, Theater ID, and Max Seats must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (java.time.format.DateTimeParseException e) {
                JOptionPane.showMessageDialog(frame, "Start Time must be in the format YYYY-MM-DD HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(frame, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void modifyShowtime(JFrame frame) {
        List<Showtime> showtimes = Showtime.fetchShowtimes();
        String[] showtimeIDs = showtimes.stream().map(s -> String.valueOf(s.getShowtimeID())).toArray(String[]::new);

        String selectedShowtimeID = (String) JOptionPane.showInputDialog(
                frame,
                "Select a showtime to modify:",
                "Modify Showtime",
                JOptionPane.PLAIN_MESSAGE,
                null,
                showtimeIDs,
                null
        );

        if (selectedShowtimeID != null) {
            Showtime showtimeToEdit = showtimes.stream()
                    .filter(s -> s.getShowtimeID() == Integer.parseInt(selectedShowtimeID))
                    .findFirst()
                    .orElse(null);

            if (showtimeToEdit != null) {
                JTextField movieIDField = new JTextField(String.valueOf(showtimeToEdit.getMovieID()));
                JTextField theaterIDField = new JTextField(String.valueOf(showtimeToEdit.getTheaterID()));
                JTextField dateTimeField = new JTextField(showtimeToEdit.getDateTime().toString());
                JTextField maxSeatsField = new JTextField(String.valueOf(showtimeToEdit.getMaxSeats()));

                Object[] message = {
                        "Movie ID:", movieIDField,
                        "Theater ID:", theaterIDField,
                        "Start Time (YYYY-MM-DD HH:MM:SS):", dateTimeField,
                        "Max Seats:", maxSeatsField
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "Modify Showtime", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        // Trim input and validate each field
                        String movieIDText = movieIDField.getText().trim();
                        String theaterIDText = theaterIDField.getText().trim();
                        String dateTimeText = dateTimeField.getText().trim();
                        String maxSeatsText = maxSeatsField.getText().trim();

                        if (movieIDText.isEmpty() || theaterIDText.isEmpty() || dateTimeText.isEmpty() || maxSeatsText.isEmpty()) {
                            throw new IllegalArgumentException("All fields must be filled!");
                        }

                        int movieID = Integer.parseInt(movieIDText);
                        int theaterID = Integer.parseInt(theaterIDText);
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        int maxSeats = Integer.parseInt(maxSeatsText);

                        if (maxSeats <= 0) {
                            throw new IllegalArgumentException("Max Seats must be greater than 0!");
                        }

                        showtimeToEdit = new Showtime(showtimeToEdit.getShowtimeID(), movieID, theaterID, dateTime, maxSeats);
                        if (showtimeToEdit.modifyShowtime()) {
                            JOptionPane.showMessageDialog(frame, "Showtime updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Failed to update showtime.");
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(frame, "Movie ID, Theater ID, and Max Seats must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    } catch (java.time.format.DateTimeParseException e) {
                        JOptionPane.showMessageDialog(frame, "Start Time must be in the format YYYY-MM-DD HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }


    public static void deleteShowtime(JFrame frame) {
        List<Showtime> showtimes = Showtime.fetchShowtimes();
        String[] showtimeIDs = showtimes.stream().map(s -> String.valueOf(s.getShowtimeID())).toArray(String[]::new);

        String selectedShowtimeID = (String) JOptionPane.showInputDialog(
                frame,
                "Select a showtime to delete:",
                "Delete Showtime",
                JOptionPane.PLAIN_MESSAGE,
                null,
                showtimeIDs,
                null
        );

        if (selectedShowtimeID != null) {
            int confirmation = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to delete showtime ID " + selectedShowtimeID + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                if (Showtime.deleteShowtime(Integer.parseInt(selectedShowtimeID))) {
                    JOptionPane.showMessageDialog(frame, "Showtime deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete showtime.");
                }
            }
        }
    }
}
