import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WelcomeView {

    public static void showMainMenu() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Movie Ticket Booking System :)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());

            JLabel welcomeLabel = new JLabel("MOVIE TICKET BOOKING SYSTEM", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Georgia", Font.BOLD, 28));
            welcomeLabel.setForeground(new Color(0, 51, 102)); // Dark blue
            welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            frame.add(welcomeLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JButton adminButton = createStyledButton("Admin");
            JButton guestButton = createStyledButton("Guest");
            JButton loginButton = createStyledButton("Login");
            JButton signInButton = createStyledButton("Sign up");

            adminButton.addActionListener(e -> AdminView.showAdminLogin(frame));
            guestButton.addActionListener(e -> GuestView.openGuestMenu(frame));
            loginButton.addActionListener(e -> loginForm(frame));
            signInButton.addActionListener(e -> showSignUpForm(frame));

            gbc.gridy = 0;
            buttonPanel.add(adminButton, gbc);
            gbc.gridy = 1;
            buttonPanel.add(guestButton, gbc);
            gbc.gridy = 2;
            buttonPanel.add(loginButton, gbc);
            gbc.gridy = 3;
            buttonPanel.add(signInButton, gbc);

            frame.add(buttonPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(200, 162, 200));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 162, 200), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(216, 191, 216));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 162, 200));
            }
        });
        return button;
    }

    private static void showSignUpForm(JFrame frame) {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        Object[] message = {
                "Full Name:", nameField,
                "Email:", emailField,
                "Password:", passwordField,
                "Phone Number (e.g. 585-589-5236):", phoneField,
                "Address:", addressField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Sign Up", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText();
            String address = addressField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name, Email, and Password are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User(name, email, password, phone, address);

            if (newUser.saveToDatabase()) {
                JOptionPane.showMessageDialog(frame, "Sign Up Successful! Welcome, " + name + "Login now to access all the features");
            } else {
                JOptionPane.showMessageDialog(frame, "Sign Up Failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void loginForm(JFrame frame) {
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Email:", emailField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            User loggedInUser = User.authenticate(email, password);
            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful! Welcome, " + loggedInUser.getName());
                showUserMenu(frame, loggedInUser);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void showUserMenu(JFrame frame, User user) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JLabel userLabel = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setForeground(new Color(0, 51, 102));
        frame.add(userLabel, BorderLayout.NORTH);

        JPanel userMenuPanel = new JPanel(new GridLayout(0, 1, 10, 10));

        JButton viewMoviesButton = new JButton("View Movies");
        JButton viewShowtimesButton = new JButton("View Showtimes");
        JButton bookTicketButton = new JButton("Book Ticket");
        JButton logoutButton = new JButton("Logout");

        // Action listener to show seating map when the "Book Ticket" button is clicked
        bookTicketButton.addActionListener(e -> {
            int showtimeId = 1; // This will come from the selected showtime
            //showSeatMap(frame, showtimeId, user);
        });

        logoutButton.addActionListener(e -> showMainMenu());

        userMenuPanel.add(viewMoviesButton);
        userMenuPanel.add(viewShowtimesButton);
        userMenuPanel.add(bookTicketButton);
        userMenuPanel.add(logoutButton);

        frame.add(userMenuPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }








}
