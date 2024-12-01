import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NewsView {

    public static void showNews(JFrame frame, Runnable backToMenuCallback) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("News", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 18));

        List<News> newsList = News.fetchAllNews();

        String[] columnNames = {"ID", "Headline", "Content"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (News news : newsList) {
            tableModel.addRow(new Object[]{news.getNewsId(), news.getHeadline(), news.getContent()});
        }

        JTable newsTable = new JTable(tableModel);
        newsTable.setRowHeight(30);
        JScrollPane tableScrollPane = new JScrollPane(newsTable);

        JButton backButton = new JButton("Back");

        backButton.addActionListener(e -> backToMenuCallback.run());

        JPanel newsPanel = new JPanel(new BorderLayout(10, 10));
        newsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        newsPanel.add(titleLabel, BorderLayout.NORTH);
        newsPanel.add(tableScrollPane, BorderLayout.CENTER);
        newsPanel.add(backButton, BorderLayout.SOUTH);

        frame.add(newsPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        VisualGui.applyGlobalFont(frame.getContentPane(), VisualGui.fontTable);
    }

    public static void addNews(JFrame frame) {
        JTextField headlineField = new JTextField();
        JTextArea contentArea = new JTextArea(5, 20);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);

        Object[] message = {
                "Headline:", headlineField,
                "Content:", contentScrollPane
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add News", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String headline = headlineField.getText().trim();
            String content = contentArea.getText().trim();

            if (headline.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Headline and Content cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            News news = new News(headline, content);
            if (news.addToDatabase()) {
                JOptionPane.showMessageDialog(frame, "News added successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Error adding news.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void modifyNews(JFrame frame) {
        List<News> newsList = News.fetchAllNews();

        String[] newsData = newsList.stream()
                .map(news -> "ID: " + news.getNewsId() + " | " + news.getHeadline())
                .toArray(String[]::new);

        String selectedNews = (String) JOptionPane.showInputDialog(
                frame,
                "Select news to modify:",
                "Modify News",
                JOptionPane.PLAIN_MESSAGE,
                null,
                newsData,
                newsData.length > 0 ? newsData[0] : null
        );

        if (selectedNews != null) {
            int newsId = Integer.parseInt(selectedNews.split("\\|")[0].split(":")[1].trim());
            News news = newsList.stream().filter(n -> n.getNewsId() == newsId).findFirst().orElse(null);

            if (news != null) {
                JTextField headlineField = new JTextField(news.getHeadline());
                JTextArea contentArea = new JTextArea(news.getContent(), 5, 20);
                JScrollPane contentScrollPane = new JScrollPane(contentArea);

                Object[] message = {
                        "Headline:", headlineField,
                        "Content:", contentScrollPane
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "Modify News", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String newHeadline = headlineField.getText().trim();
                    String newContent = contentArea.getText().trim();

                    if (newHeadline.isEmpty() || newContent.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Headline and Content cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    news.setHeadline(newHeadline);
                    news.setContent(newContent);

                    if (news.modifyInDatabase()) {
                        JOptionPane.showMessageDialog(frame, "News updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error updating news.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public static void deleteNews(JFrame frame) {
        List<News> newsList = News.fetchAllNews();

        String[] newsData = newsList.stream()
                .map(news -> "ID: " + news.getNewsId() + " | " + news.getHeadline())
                .toArray(String[]::new);

        String selectedNews = (String) JOptionPane.showInputDialog(
                frame,
                "Select news to delete:",
                "Delete News",
                JOptionPane.PLAIN_MESSAGE,
                null,
                newsData,
                newsData.length > 0 ? newsData[0] : null
        );

        if (selectedNews != null) {
            int newsId = Integer.parseInt(selectedNews.split("\\|")[0].split(":")[1].trim());
            News news = newsList.stream().filter(n -> n.getNewsId() == newsId).findFirst().orElse(null);

            if (news != null) {
                int option = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to delete this news?\nHeadline: " + news.getHeadline(),
                        "Delete News",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    if (news.deleteFromDatabase()) {
                        JOptionPane.showMessageDialog(frame, "News deleted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error deleting news.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}
