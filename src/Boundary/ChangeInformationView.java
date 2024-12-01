package Boundary;
import Visual.*;
import javax.swing.*;
import java.awt.*;
import Domain.*;
public class ChangeInformationView {

    public static void showChangeInformation(JFrame frame, User loggedInUser, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        JLabel titleLabel = new JLabel("Change User Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        panel.add(titleLabel);

        JButton changeFullNameButton = VisualGui.createStyledButtonSmall("Change Full Name");
        JButton changeEmailButton = VisualGui.createStyledButtonSmall("Change Email");
        JButton changePasswordButton = VisualGui.createStyledButtonSmall("Change Password");

        // change full name button
        changeFullNameButton.addActionListener(e -> {
            JPanel namePanel = new JPanel(new GridLayout(0, 1));
            JTextField firstNameField = new JTextField(loggedInUser.getName().split(" ")[0]);
            JTextField lastNameField = new JTextField(loggedInUser.getName().contains(" ") ? loggedInUser.getName().split(" ")[1] : "");

            namePanel.add(new JLabel("First Name:"));
            namePanel.add(firstNameField);
            namePanel.add(new JLabel("Last Name:"));
            namePanel.add(lastNameField);

            int result = JOptionPane.showConfirmDialog(frame, namePanel, "Change Full Name", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newFirstName = firstNameField.getText().trim();
                String newLastName = lastNameField.getText().trim();
                if (newFirstName.isEmpty() || newLastName.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Both first and last names are required.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    boolean updateSuccess = loggedInUser.updateInformation(newFirstName + " " + newLastName, loggedInUser.getEmail(), null);
                    if (updateSuccess) {
                        JOptionPane.showMessageDialog(frame, "Full name updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to update full name. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // change email
        changeEmailButton.addActionListener(e -> {
            JPanel emailPanel = new JPanel(new GridLayout(0, 1));
            JTextField emailField = new JTextField(loggedInUser.getEmail());

            emailPanel.add(new JLabel("New Email:"));
            emailPanel.add(emailField);

            int result = JOptionPane.showConfirmDialog(frame, emailPanel, "Change Email", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newEmail = emailField.getText().trim();
                if (newEmail.isEmpty() || !newEmail.contains("@")) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    boolean updateSuccess = loggedInUser.updateInformation(loggedInUser.getName(), newEmail, null);
                    if (updateSuccess) {
                        JOptionPane.showMessageDialog(frame, "Email updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to update email. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // change password
        changePasswordButton.addActionListener(e -> {
            JPanel passwordPanel = new JPanel(new GridLayout(0, 1));
            JPasswordField passwordField = new JPasswordField();
            JPasswordField confirmPasswordField = new JPasswordField();

            passwordPanel.add(new JLabel("Enter New Password:"));
            passwordPanel.add(passwordField);
            passwordPanel.add(new JLabel("Confirm New Password:"));
            passwordPanel.add(confirmPasswordField);

            int result = JOptionPane.showConfirmDialog(frame, passwordPanel, "Change Password", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newPassword = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(frame, "Passwords do not match or are empty. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    boolean updateSuccess = loggedInUser.updateInformation(loggedInUser.getName(), loggedInUser.getEmail(), newPassword);
                    if (updateSuccess) {
                        JOptionPane.showMessageDialog(frame, "Password updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to update password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(changeFullNameButton);
        panel.add(changeEmailButton);
        panel.add(changePasswordButton);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back to Menu");

        backButton.addActionListener(e -> backToMenuCallback.run());

        buttonPanel.add(backButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
    }
}