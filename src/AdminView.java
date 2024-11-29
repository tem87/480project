import javax.swing.*;
import java.awt.*;

public class AdminView {

    public static void showAdminLogin(JFrame frame) {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel usernameLabel = VisualGui.createStyledLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = VisualGui.createStyledLabel("Password:");
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
        String adminPassword = "admin12345";

        return username.equals(adminUsername) && password.equals(adminPassword);
    }

    public static void openAdminMenu(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Styled title using VisualGui
        JLabel adminLabel = VisualGui.createStyledTitle("Admin Menu");

        // Main panel with GridBagLayout for centering buttons
        JPanel adminPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure buttons fill the width
        gbc.insets = new Insets(5, 0, 5, 0); // Add spacing between buttons
        gbc.gridx = 0; // All buttons in one column

        // Create styled buttons
        JButton viewTheatreButton = createSmallStyledButton("View Theatres", () ->
                TheatreView.showTheatre(frame, () -> AdminView.openAdminMenu(frame))
        );
        JButton viewMovieButton = createSmallStyledButton("View Movies", () ->
                MovieView.showMoviesAdminView(frame, () -> AdminView.openAdminMenu(frame))
        );
        JButton addMovieButton = createSmallStyledButton("Add Movie", () ->
                MovieView.addMovie(frame)
        );
        JButton modifyMovieButton = createSmallStyledButton("Modify Movie", () ->
                MovieView.modifyMovie(frame)
        );
        JButton deleteMovieButton = createSmallStyledButton("Delete Movie", () ->
                MovieView.deleteMovie(frame)
        );
        JButton viewShowtimeButton = createSmallStyledButton("View Showtimes", () ->
                ShowtimeView.showShowtime(frame, () -> AdminView.openAdminMenu(frame))
        );
        JButton addShowtimeButton = createSmallStyledButton("Add Showtime", () ->
                ShowtimeView.addShowtime(frame)
        );
        JButton modifyShowtimeButton = createSmallStyledButton("Modify Showtime", () ->
                ShowtimeView.modifyShowtime(frame)
        );
        JButton deleteShowtimeButton = createSmallStyledButton("Delete Showtime", () ->
                ShowtimeView.deleteShowtime(frame)
        );
        JButton viewNewsButton = createSmallStyledButton("View News", () ->
                NewsView.showNews(frame, () -> AdminView.openAdminMenu(frame))
        );
        JButton addNewsButton = createSmallStyledButton("Add News", () ->
                NewsView.addNews(frame)
        );
        JButton modifyNewsButton = createSmallStyledButton("Modify News", () ->
                NewsView.modifyNews(frame)
        );
        JButton deleteNewsButton = createSmallStyledButton("Delete News", () ->
                NewsView.deleteNews(frame)
        );
        JButton backButton = createSmallStyledButton("Back to Main Menu", WelcomeView::showMainMenu);

        // Add buttons to the panel with vertical centering
        gbc.gridy = 0;
        adminPanel.add(viewTheatreButton, gbc);
        gbc.gridy++;
        adminPanel.add(viewMovieButton, gbc);
        gbc.gridy++;
        adminPanel.add(addMovieButton, gbc);
        gbc.gridy++;
        adminPanel.add(modifyMovieButton, gbc);
        gbc.gridy++;
        adminPanel.add(deleteMovieButton, gbc);
        gbc.gridy++;
        adminPanel.add(viewShowtimeButton, gbc);
        gbc.gridy++;
        adminPanel.add(addShowtimeButton, gbc);
        gbc.gridy++;
        adminPanel.add(modifyShowtimeButton, gbc);
        gbc.gridy++;
        adminPanel.add(deleteShowtimeButton, gbc);
        gbc.gridy++;
        adminPanel.add(viewNewsButton, gbc);
        gbc.gridy++;
        adminPanel.add(addNewsButton, gbc);
        gbc.gridy++;
        adminPanel.add(modifyNewsButton, gbc);
        gbc.gridy++;
        adminPanel.add(deleteNewsButton, gbc);
        gbc.gridy++;
        adminPanel.add(backButton, gbc);

        // Add components to the frame
        frame.add(adminLabel, BorderLayout.NORTH);
        frame.add(adminPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // Helper method for smaller buttons
    private static JButton createSmallStyledButton(String text, Runnable action) {
        JButton button = VisualGui.createStyledButton(text, action);
        button.setPreferredSize(new Dimension(200, 40)); // Set a smaller preferred size
        return button;
    }

}

