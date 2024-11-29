import javax.swing.*;
import java.awt.*;

public class AdminView {

    public static void showAdminLogin(JFrame frame) {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(
                frame, loginPanel, "Admin Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (validateAdminCredentials(username, password)) {
                openAdminMenu(frame);
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "Invalid username or password. Access denied.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private static boolean validateAdminCredentials(String username, String password) {
        // Hardcoded credentials - replace with database queries if necessary
        String adminUsername = "admin";
        String adminPassword = "gojo123";

        return username.equals(adminUsername) && password.equals(adminPassword);
    }

    public static void openAdminMenu(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridLayout(0, 1, 10, 10));

        JLabel adminLabel = new JLabel("Admin Menu", SwingConstants.CENTER);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 18));
        adminPanel.add(adminLabel);

        JButton viewTheatreButton = new JButton("View Theatres");
        JButton viewMovieButton = new JButton("View Movies");
        JButton addMovieButton = new JButton("Add Movie");
        JButton modifyMovieButton = new JButton("Modify Movie");
        JButton deleteMovieButton = new JButton("Delete Movie");
        JButton viewShowtimeButton = new JButton("View Showtimes");
        JButton addShowtimeButton = new JButton("Add Showtime");
        JButton modifyShowtimeButton = new JButton("Modify Showtime");
        JButton deleteShowtimeButton = new JButton("Delete Showtime");
        JButton viewNewsButton = new JButton("View News");
        JButton addNewsButton = new JButton("Add News");
        JButton modifyNewsButton = new JButton("Modify News");
        JButton deleteNewsButton = new JButton("Delete News");
        JButton backButton = new JButton("Back to Main Menu");

        // Action listeners for Admin-specific functionality
        viewTheatreButton.addActionListener(e -> TheatreView.showTheatre(frame, () -> AdminView.openAdminMenu(frame)));
        viewMovieButton.addActionListener(e -> MovieView.showMoviesAdminView(frame, () -> AdminView.openAdminMenu(frame)));
        addMovieButton.addActionListener(e -> MovieView.addMovie(frame));
        modifyMovieButton.addActionListener(e -> MovieView.modifyMovie(frame));
        deleteMovieButton.addActionListener(e -> MovieView.deleteMovie(frame));
        viewShowtimeButton.addActionListener(e -> ShowtimeView.showShowtime(frame, () -> AdminView.openAdminMenu(frame)));
        addShowtimeButton.addActionListener(e -> ShowtimeView.addShowtime(frame));
        modifyShowtimeButton.addActionListener(e -> ShowtimeView.modifyShowtime(frame));
        deleteShowtimeButton.addActionListener(e -> ShowtimeView.deleteShowtime(frame));
        viewNewsButton.addActionListener(e -> NewsView.showNews(frame, () -> AdminView.openAdminMenu(frame)));
        addNewsButton.addActionListener(e -> NewsView.addNews(frame));
        modifyNewsButton.addActionListener(e -> NewsView.modifyNews(frame));
        deleteNewsButton.addActionListener(e -> NewsView.deleteNews(frame));

        // Back button callback
        backButton.addActionListener(e ->
                WelcomeView.showMainMenu()
        );


        // Adding buttons to the Admin panel
        adminPanel.add(viewTheatreButton);
        adminPanel.add(viewMovieButton);
        adminPanel.add(addMovieButton);
        adminPanel.add(modifyMovieButton);
        adminPanel.add(deleteMovieButton);
        adminPanel.add(viewShowtimeButton);
        adminPanel.add(addShowtimeButton);
        adminPanel.add(modifyShowtimeButton);
        adminPanel.add(deleteShowtimeButton);
        adminPanel.add(viewNewsButton);
        adminPanel.add(addNewsButton);
        adminPanel.add(modifyNewsButton);
        adminPanel.add(deleteNewsButton);
        adminPanel.add(backButton);

        frame.add(adminPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
}
