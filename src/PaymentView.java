import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PaymentView {

    public static void showPaymentView(JFrame frame, Theatre theatre, Movie movie, Showtime showtime, List<Seat> selectedSeats, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        JLabel titleLabel = new JLabel("Confirm Your Payment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel);

        // Display selected seats
        StringBuilder seatNumbers = new StringBuilder();
        for (Seat seat : selectedSeats) {
            seatNumbers.append(seat.getSeatNumber()).append(", ");
        }
        panel.add(new JLabel("Selected Seats: " + seatNumbers.toString(), SwingConstants.CENTER));

        // Payment method dropdown
        JComboBox<String> paymentMethodDropdown = new JComboBox<>(new String[]{"Credit Card", "Debit Card"});
        panel.add(new JLabel("Payment Method:", SwingConstants.CENTER));
        panel.add(paymentMethodDropdown);

        // Payment details
        JTextField nameField = new JTextField();
        JTextField cardNumberField = new JTextField();
        JPasswordField cvvField = new JPasswordField();
        JTextField expirationDateField = new JTextField();

        panel.add(new JLabel("Name:", SwingConstants.CENTER));
        panel.add(nameField);
        panel.add(new JLabel("Card Number:", SwingConstants.CENTER));
        panel.add(cardNumberField);
        panel.add(new JLabel("CVV:", SwingConstants.CENTER));
        panel.add(cvvField);
        panel.add(new JLabel("Expiration Date (MM/YY):", SwingConstants.CENTER));
        panel.add(expirationDateField);

        // Confirm button
        JButton confirmButton = new JButton("Confirm Payment");
        confirmButton.addActionListener(e -> {
            String name = nameField.getText();
            String cardNumber = cardNumberField.getText();
            String cvv = new String(cvvField.getPassword());
            String expirationDate = expirationDateField.getText();
            String paymentMethod = (String) paymentMethodDropdown.getSelectedItem();

            if (validateInputs(paymentMethod, name, cardNumber, cvv, expirationDate)) {
                for (Seat seat : selectedSeats) {
                    seat.reserveSeat(seat.getSeatId()); // Reserve each selected seat
                }
                JOptionPane.showMessageDialog(frame, "Payment Successful!\n" +
                        "Theatre: " + theatre.getName() +
                        "\nMovie: " + movie.getTitle() +
                        "\nShowtime: " + showtime.getDateTime() +
                        "\nSeats: " + seatNumbers.toString() +
                        "\nPayment Method: " + paymentMethod);
                backToMenuCallback.run();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Payment Details. Please try again.");
            }
        });

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> SeatSelectionView.showSeatMap(frame, showtime.getShowtimeID(), backToMenuCallback, theatre, movie, showtime));

        panel.add(confirmButton);
        panel.add(backButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // Validate payment details
//    private static boolean validateInputs(String name, String cardNumber, String cvv, String expirationDate) {
//        return !(name.isEmpty() || !cardNumber.matches("\\d{16}") || !cvv.matches("\\d{3}") || !expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}"));
//    }
    // Validate inputs for payment details
    private static boolean validateInputs(String method, String name, String cardNumber, String cvv, String expirationDate) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) { // Must be 16 digits
            return false;
        }
        if (cvv == null || !cvv.matches("\\d{3}")) { // Must be 3 digits
            return false;
        }
        if (expirationDate == null || !expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}")) { // MM/YY format
            return false;
        }
        return true;
    }
}

