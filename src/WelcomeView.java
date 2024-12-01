import javax.swing.*;
import java.awt.*;

public class WelcomeView {

    public static void showMainMenu() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Movie Ticket Booking System :)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 800);
            frame.setLayout(new BorderLayout());
            JLabel welcomeLabel = VisualGui.createStyledTitle("WELCOME TO ACMEPLEX!");
            frame.add(welcomeLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JButton adminButton = VisualGui.createStyledButton("Admin", () -> AdminView.showAdminLogin(frame));
            JButton guestButton = VisualGui.createStyledButton("Guest", () -> GuestView.openGuestMenu(frame));
            JButton loginButton = VisualGui.createStyledButton("Login", () -> RegisteredUserView.login(frame));
            JButton signInButton = VisualGui.createStyledButton("Sign up", () -> showSignUpForm(frame));

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

    private static void showSignUpForm(JFrame frame) {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        restrictToChar(nameField, 50);
        restrictToPhoneFormat(phoneField, 12);

        Object[] message = {
                "Full Name:", nameField,
                "Email:", emailField,
                "Password:", passwordField,
                "Phone Number (e.g. 585-000-5236):", phoneField,
                "Address:", addressField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Sign Up", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText();
            String address = addressField.getText();

            String emailRegex = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(emailRegex)) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name, Email, and Password are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RegisteredUser newUser = new RegisteredUser(name, email, password, phone, address);

            if (newUser.saveToDatabase()) {
                JOptionPane.showMessageDialog(frame, "Sign Up Successful! Welcome to AcmePlex, " + name + ", you can now Log In to access all the features!");
            } else {
                JOptionPane.showMessageDialog(frame, "Sign Up Failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private static void restrictToDigits(JTextField field, int maxLength) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || field.getText().length() >= maxLength) {
                    e.consume();
                }
            }
        });
    }

    private static void restrictToChar(JTextField field, int maxLength) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c) || field.getText().length() >= maxLength) {
                    e.consume();
                }
            }
        });
    }

    private static void restrictToPhoneFormat(JTextField field, int maxLength) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = field.getText();

                if ((!Character.isDigit(c) && c != '-') || currentText.length() >= maxLength) {
                    e.consume();
                }
                if (currentText.endsWith("-") && c == '-' || currentText.isEmpty() && c == '-') {
                    e.consume();
                }
            }
        });
    }
}
