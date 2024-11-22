import javax.swing.*;
import java.awt.*;

public class RegisteredUserView {

    public static void openRegisteredUserMenu(JFrame frame, RegisteredUser loggedInUser) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel registeredUserPanel = new JPanel();
        registeredUserPanel.setLayout(new GridLayout(0, 1, 10, 10));

        JLabel registeredUserLabel = new JLabel("Registered User Menu", SwingConstants.CENTER);
        registeredUserLabel.setFont(new Font("Arial", Font.BOLD, 18));
        registeredUserPanel.add(registeredUserLabel);

        JButton viewTheatreButton = new JButton("View Theatres");
        JButton viewMovieButton = new JButton("View Movies");
        JButton viewearlyMovieButton = new JButton("View Early Access Movies");
        JButton viewShowtimeButton = new JButton("View Showtimes");
        JButton purchaseTicketButton = new JButton("Purchase Ticket");
        JButton cancelTicketButton = new JButton("Cancel Ticket");
        JButton annualFeeButton = new JButton("Pay Annual Fee");
        JButton newsButton = new JButton("News");
        JButton informationButton = new JButton("Change Information");
        JButton backButton = new JButton("Logout");

        // Update callbacks for the registered user functionality
        viewTheatreButton.addActionListener(e ->
                TheatreView.showTheatre(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );
        viewMovieButton.addActionListener(e ->
                MovieView.showMovie(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );
        viewearlyMovieButton.addActionListener(e ->
                MovieView.showEarlyAccessMovies(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );
        viewShowtimeButton.addActionListener(e ->
                ShowtimeView.showShowtimeDetails(frame, () -> openRegisteredUserMenu(frame, loggedInUser))
        );

        purchaseTicketButton.addActionListener(e ->
                PurchaseTicketViewRU.showPurchaseTicketView(frame, loggedInUser, () -> RegisteredUserView.openRegisteredUserMenu(frame, loggedInUser))
        );




        // Logout functionality
        backButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "You have been logged out.");
            WelcomeView.showMainMenu();
        });

        registeredUserPanel.add(viewTheatreButton);
        registeredUserPanel.add(viewMovieButton);
        registeredUserPanel.add(viewearlyMovieButton);
        registeredUserPanel.add(viewShowtimeButton);
        registeredUserPanel.add(purchaseTicketButton);
        registeredUserPanel.add(cancelTicketButton);
        registeredUserPanel.add(annualFeeButton);
        registeredUserPanel.add(newsButton);
        registeredUserPanel.add(informationButton);
        registeredUserPanel.add(backButton);

        frame.add(registeredUserPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
    //        cancelTicketButton.addActionListener(e -> {
//            // Implement cancel ticket logic for registered users
//            CancelTicketView.showCancelTicketView(frame, loggedInUser, () -> openRegisteredUserMenu(frame, loggedInUser));
//        });
//
//        // Annual Fee Payment
//        annualFeeButton.addActionListener(e -> {
//            boolean feePaid = AnnualFeePaymentView.payAnnualFee(frame, loggedInUser);
//            if (feePaid) {
//                JOptionPane.showMessageDialog(frame, "Annual fee payment successful!");
//                loggedInUser.setAnnualFeePaid(true); // Update user state
//            } else {
//                JOptionPane.showMessageDialog(frame, "Failed to process annual fee payment.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
    public static void login(JFrame frame) {
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Email:", emailField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Registered User Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Authenticate the registered user
            RegisteredUser loggedInUser = RegisteredUser.authenticate(email, password);
            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful! Welcome, " + loggedInUser.getName());
                openRegisteredUserMenu(frame, loggedInUser);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

