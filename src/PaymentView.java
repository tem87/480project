import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PaymentView {

    public static void showPaymentView(JFrame frame, Theatre theatre, Movie movie, Showtime showtime, List<Seat> selectedSeats, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout(10, 10));

        // Main Title
        JLabel mainTitle = new JLabel("Enter Payment Details", SwingConstants.CENTER);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(mainTitle, BorderLayout.NORTH);

        // Main panel for all sections
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        // Section 1: Movie and Seat Details
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Movie & Seat Details", 0, 0, new Font("Arial", Font.BOLD, 16), Color.BLACK));
        StringBuilder seatNumbers = new StringBuilder();
        for (Seat seat : selectedSeats) {
            seatNumbers.append(seat.getSeatNumber()).append(", ");
        }
        detailsPanel.add(new JLabel("Movie: " + movie.getTitle(), SwingConstants.LEFT));
        detailsPanel.add(new JLabel("Showtime: " + showtime.getDateTime(), SwingConstants.LEFT));
        detailsPanel.add(new JLabel("Selected Seats: " + seatNumbers.toString(), SwingConstants.LEFT));
        mainPanel.add(detailsPanel);

        // Section 2: Pricing Details
        JPanel pricingPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        pricingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Pricing Details", 0, 0, new Font("Arial", Font.BOLD, 16), Color.BLACK));
        double pricePerSeat = movie.getPrice();
        double totalPrice = pricePerSeat * selectedSeats.size();
        double tax = totalPrice * 0.05; // 5% tax
        double totalPriceAfterTax = totalPrice + tax;
        pricingPanel.add(new JLabel(String.format("Price Per Seat: $%.2f", pricePerSeat), SwingConstants.LEFT));
        pricingPanel.add(new JLabel(String.format("Total Price (Before Tax): $%.2f", totalPrice), SwingConstants.LEFT));
        pricingPanel.add(new JLabel(String.format("Tax (5%%): $%.2f", tax), SwingConstants.LEFT));
        pricingPanel.add(new JLabel(String.format("Total Price (After Tax): $%.2f", totalPriceAfterTax), SwingConstants.LEFT));
        mainPanel.add(pricingPanel);

        // Section 3: Payment Form
        JPanel paymentPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Payment Information", 0, 0, new Font("Arial", Font.BOLD, 16), Color.BLACK));
        JComboBox<String> paymentMethodDropdown = new JComboBox<>(new String[]{"Credit Card", "Debit Card"});
        JTextField nameField = new JTextField();
        JTextField cardNumberField = new JTextField();
        JPasswordField cvvField = new JPasswordField();
        JTextField expirationDateField = new JTextField();
        paymentPanel.add(new JLabel("Payment Method:", SwingConstants.RIGHT));
        paymentPanel.add(paymentMethodDropdown);
        paymentPanel.add(new JLabel("Name:", SwingConstants.RIGHT));
        paymentPanel.add(nameField);
        paymentPanel.add(new JLabel("Card Number:", SwingConstants.RIGHT));
        paymentPanel.add(cardNumberField);
        paymentPanel.add(new JLabel("CVV:", SwingConstants.RIGHT));
        paymentPanel.add(cvvField);
        paymentPanel.add(new JLabel("Expiration Date (MM/YY):", SwingConstants.RIGHT));
        paymentPanel.add(expirationDateField);
        mainPanel.add(paymentPanel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
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
                JOptionPane.showMessageDialog(frame, String.format(
                        "Payment Successful!\nTheatre: %s\nMovie: %s\nShowtime: %s\nSeats: %s\nPayment Method: %s\nTotal Price (After Tax): $%.2f",
                        theatre.getName(), movie.getTitle(), showtime.getDateTime(), seatNumbers.toString(), paymentMethod, totalPriceAfterTax
                ));
                backToMenuCallback.run();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Payment Details. Please try again.");
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> SeatSelectionView.showSeatMap(frame, showtime.getShowtimeID(), backToMenuCallback, theatre, movie, showtime));

        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        // Add everything to the frame
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
    }

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
