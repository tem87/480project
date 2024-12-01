package Visual;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class VisualGui {

    public static final Font fontTitle = new Font("Courier New", Font.BOLD, 20);
    public static final Font fontTable = new Font("Courier New", Font.PLAIN, 14);
    public static final Font fontHeader = new Font("Courier New", Font.BOLD, 16);
    public static final Font fontButton = new Font("Courier New", Font.BOLD, 14);
    public static final Color primaryColor = new Color(173, 216, 230);
    public static final Color textColor = Color.black;
    public static final Color rowColor = new Color(224, 255, 255);
    public static final Color backgroundColor= Color.white;

    public static JLabel createStyledTitle(String text) {
        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(fontTitle);
        titleLabel.setForeground(textColor);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(primaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return titleLabel;
    }

    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(fontTable);
        label.setForeground(textColor);
        return label;
    }

    public static JButton createStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(fontButton);
        button.setBackground(primaryColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addActionListener(e -> action.run());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor);
            }
        });

        return button;
    }

    public static JButton createStyledButtonSmall(String text) {
        JButton button = new JButton(text);
        button.setFont(fontButton);
        button.setBackground(primaryColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        //button.addActionListener(e -> action.run());


        return button;
    }

    public static JTable createStyledTable(Object[][] data, String[] columnNames) {
        JTable table = new JTable(data, columnNames) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? rowColor : backgroundColor);
                }
                return c;
            }
        };

        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(fontTable);
        styleTableHeader(table.getTableHeader());

        return table;
    }

    private static void styleTableHeader(JTableHeader tableHeader) {
        tableHeader.setFont(fontHeader);
        tableHeader.setBackground(primaryColor);
        tableHeader.setForeground(textColor);
    }

    public static void applyGlobalFont(Container container, Font font) {
        for (Component component : container.getComponents()) {
            component.setFont(font);
            if (component instanceof Container) {
                applyGlobalFont((Container) component, font);
            }
        }
    }
}
