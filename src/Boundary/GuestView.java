package Boundary;
import Visual.*;
import Domain.*;
import javax.swing.*;
import java.awt.*;

public class GuestView {

    public static void openGuestMenu(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JLabel guestPanelLabel = VisualGui.createStyledTitle("AcmePlex Guest Menu");
        frame.add(guestPanelLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JButton viewTheatreButton = VisualGui.createStyledButton("View Theatres",
                () -> TheatreView.showTheatre(frame, () -> GuestView.openGuestMenu(frame))
        );

        JButton viewMovieButton = VisualGui.createStyledButton("View Movies",
                () -> MovieView.showMovie(frame, () -> GuestView.openGuestMenu(frame))
        );

        JButton viewShowtimeButton = VisualGui.createStyledButton("View Showtimes",
                () -> ShowtimeView.showShowtimeDetails(frame, () -> GuestView.openGuestMenu(frame))
        );

        JButton purchaseTicketButton = VisualGui.createStyledButton("Purchase Ticket",
                () -> PurchaseTicketViewGuest.showPurchaseTicketView(frame, () -> GuestView.openGuestMenu(frame))
        );

        JButton cancelTicketButton = VisualGui.createStyledButton("Cancel Ticket",
                () -> CancelTicketViewGuest.showCancelTicketView(frame, () -> GuestView.openGuestMenu(frame))
        );

        JButton backButton = VisualGui.createStyledButton("Back to Main Menu",
                WelcomeView::showMainMenu
        );

        gbc.gridy = 0;
        buttonPanel.add(viewTheatreButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(viewMovieButton, gbc);
        gbc.gridy = 2;
        buttonPanel.add(viewShowtimeButton, gbc);
        gbc.gridy = 3;
        buttonPanel.add(purchaseTicketButton, gbc);
        gbc.gridy = 4;
        buttonPanel.add(cancelTicketButton, gbc);
        gbc.gridy = 5;
        buttonPanel.add(backButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
}
