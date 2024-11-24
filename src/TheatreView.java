import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TheatreView {

    public static void showTheatre(JFrame frame, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        java.util.List<Theatre> theaters = Theatre.fetchTheaters();

        String[] columnNames = {"Name", "Location"};

        Object[][] data = new Object[theaters.size()][columnNames.length];
        for (int i = 0; i < theaters.size(); i++) {
            Theatre theater = theaters.get(i);
            data[i][0] = theater.getName();
            data[i][1] = theater.getLocation();
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(table);

        // Back button with a dynamic callback
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> backToMenuCallback.run());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }


    public static void addTheatre(JFrame frame) {
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();

        Object[] message = {
                "Theater Name:", nameField,
                "Location:", locationField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add New Theater", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();

            // Validate input fields
            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Theatre newTheater = new Theatre(name, location);
            if (newTheater.addTheatre()) {
                JOptionPane.showMessageDialog(frame, "Theater added successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add theater.");
            }
        }
    }


    public static void modifyTheatre(JFrame frame) {
        List<Theatre> theaters = Theatre.fetchTheaters();
        String[] theaterNames = theaters.stream().map(Theatre::getName).toArray(String[]::new);

        String selectedTheater = (String) JOptionPane.showInputDialog(
                frame,
                "Select a theater to modify:",
                "Modify Theater",
                JOptionPane.PLAIN_MESSAGE,
                null,
                theaterNames,
                null
        );

        if (selectedTheater != null) {
            Theatre theaterToEdit = theaters.stream()
                    .filter(theater -> theater.getName().equals(selectedTheater))
                    .findFirst()
                    .orElse(null);

            if (theaterToEdit != null) {
                JTextField nameField = new JTextField(theaterToEdit.getName());
                JTextField locationField = new JTextField(theaterToEdit.getLocation());

                Object[] message = {
                        "Theater Name:", nameField,
                        "Location:", locationField
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "Modify Theater", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String name = nameField.getText().trim();
                    String location = locationField.getText().trim();

                    // Validate input fields
                    if (name.isEmpty() || location.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    theaterToEdit = new Theatre(
                            theaterToEdit.getTheatreID(),
                            name,
                            location
                    );

                    if (theaterToEdit.updateTheater()) {
                        JOptionPane.showMessageDialog(frame, "Theater updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to update theater.");
                    }
                }
            }
        }
    }

}
