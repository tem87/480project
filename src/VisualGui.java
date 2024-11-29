import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class VisualGui {

    // Common font styles
    public static final Font TITLE_FONT = new Font("Courier New", Font.BOLD, 20);
    public static final Font TABLE_FONT = new Font("Courier New", Font.PLAIN, 14);
    public static final Font TABLE_HEADER_FONT = new Font("Courier New", Font.BOLD, 16);
    public static final Font BUTTON_FONT = new Font("Courier New", Font.BOLD, 14);

    // Common colors
    public static final Color PRIMARY_COLOR = new Color(0, 128, 128);
    public static final Color PRIMARY_TEXT_COLOR = Color.black;
    public static final Color ALTERNATE_ROW_COLOR = new Color(224, 255, 255);
    public static final Color BACKGROUND_COLOR = Color.white;



    // Style JLabel for titles
    public static JLabel createStyledTitle(String text) {
        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_TEXT_COLOR);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return titleLabel;
    }
    // Create styled JLabel
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TABLE_FONT); // Use a consistent font style
        label.setForeground(PRIMARY_TEXT_COLOR);
        return label;
    }


    // Style JButton
    public static JButton createStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(PRIMARY_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addActionListener(e -> action.run());

        // Adding hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker()); // Darker shade on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR); // Original color when mouse exits
            }
        });

        return button;
    }

    public static JButton createStyledButtonSmall(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(PRIMARY_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        //button.addActionListener(e -> action.run());


        return button;
    }


    // Style JTable with alternating row colors
    public static JTable createStyledTable(Object[][] data, String[] columnNames) {
        JTable table = new JTable(data, columnNames) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? ALTERNATE_ROW_COLOR : BACKGROUND_COLOR);
                }
                return c;
            }
        };

        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(TABLE_FONT);
        styleTableHeader(table.getTableHeader());

        return table;
    }

    // Style JTable header
    private static void styleTableHeader(JTableHeader tableHeader) {
        tableHeader.setFont(TABLE_HEADER_FONT);
        tableHeader.setBackground(PRIMARY_COLOR);
        tableHeader.setForeground(PRIMARY_TEXT_COLOR);
    }


    // Apply font recursively to all components within a container
    public static void applyGlobalFont(Container container, Font font) {
        for (Component component : container.getComponents()) {
            component.setFont(font);
            if (component instanceof Container) {
                applyGlobalFont((Container) component, font); // Recursively apply to child containers
            }
        }
    }
}
