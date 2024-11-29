import javax.swing.*;
import javax.swing.table.TableCellRenderer;
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

        JTable table = VisualGui.createStyledTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        JLabel titleLabel = VisualGui.createStyledTitle("Available Theatres");

        JButton backButton = VisualGui.createStyledButton("Back to Menu", backToMenuCallback);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }


//    public static void showTheatre(JFrame frame, Runnable backToMenuCallback) {
//        frame.getContentPane().removeAll();
//        frame.setLayout(new BorderLayout());
//
//        java.util.List<Theatre> theaters = Theatre.fetchTheaters();
//        String[] columnNames = {"Name", "Location"};
//
//        Object[][] data = new Object[theaters.size()][columnNames.length];
//        for (int i = 0; i < theaters.size(); i++) {
//            Theatre theater = theaters.get(i);
//            data[i][0] = theater.getName();
//            data[i][1] = theater.getLocation();
//        }
//
//        // Create table with alternating row colors
//        JTable table = new JTable(data, columnNames) {
//            @Override
//            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
//                Component c = super.prepareRenderer(renderer, row, column);
//                if (!isRowSelected(row)) {
//                    c.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
//                }
//                return c;
//            }
//        };
//
//        table.setFillsViewportHeight(true);
//        table.setRowHeight(30);
//        table.setFont(new Font("Serif", Font.PLAIN, 14));
//        table.getTableHeader().setFont(new Font("Serif", Font.BOLD, 16));
//        table.getTableHeader().setBackground(new Color(70, 130, 180));
//        table.getTableHeader().setForeground(Color.WHITE);
//
//        JScrollPane scrollPane = new JScrollPane(table);
//
//        // Title Label
//        JLabel titleLabel = new JLabel("Available Theatres", JLabel.CENTER);
//        titleLabel.setFont(new Font("Courier New", Font.BOLD, 20));
//        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        titleLabel.setOpaque(true);
//        //titleLabel.setBackground(new Color(70, 130, 180));
//        titleLabel.setForeground(Color.WHITE);
//
//        // Styled Back Button
//        JButton backButton = new JButton("Back to Menu");
//        backButton.setFont(new Font("Courier New", Font.BOLD, 14));
//        backButton.setBackground(new Color(70, 130, 180));
//        backButton.setForeground(Color.WHITE);
//        backButton.setFocusPainted(false);
//        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
//        backButton.addActionListener(e -> backToMenuCallback.run());
//
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.add(backButton);
//
//        // Main panel with all components
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        mainPanel.add(titleLabel, BorderLayout.NORTH);
//        mainPanel.add(scrollPane, BorderLayout.CENTER);
//        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
//
//        frame.add(mainPanel, BorderLayout.CENTER);
//        frame.revalidate();
//        frame.repaint();
//    }
//public static void showTheatre(JFrame frame, Runnable backToMenuCallback) {
//    frame.getContentPane().removeAll();
//    frame.setLayout(new BorderLayout());
//
//    java.util.List<Theatre> theaters = Theatre.fetchTheaters();
//
//    String[] columnNames = {"Name", "Location"};
//
//    Object[][] data = new Object[theaters.size()][columnNames.length];
//    for (int i = 0; i < theaters.size(); i++) {
//        Theatre theater = theaters.get(i);
//        data[i][0] = theater.getName();
//        data[i][1] = theater.getLocation();
//    }
//
//    JTable table = new JTable(data, columnNames);
//    table.setFillsViewportHeight(true);
//    table.setRowHeight(30);
//    table.setFont(new Font("Arial", Font.PLAIN, 14));
//    table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
//
//    JScrollPane scrollPane = new JScrollPane(table);
//
//    // Back button with a dynamic callback
//    JButton backButton = new JButton("Back to Menu");
//    backButton.addActionListener(e -> backToMenuCallback.run());
//
//    JPanel panel = new JPanel(new BorderLayout());
//    panel.add(scrollPane, BorderLayout.CENTER);
//    panel.add(backButton, BorderLayout.SOUTH);
//
//    frame.add(panel, BorderLayout.CENTER);
//    frame.revalidate();
//    frame.repaint();
//}



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
