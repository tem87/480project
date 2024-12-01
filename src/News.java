import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class News {
    private int newsId;
    private String headline;
    private String content;

    // ctor
    public News(int newsId, String headline, String content) {
        this.newsId = newsId;
        this.headline = headline;
        this.content = content;
    }

    // ctor for creating news
    public News(String headline, String content) {
        this.headline = headline;
        this.content = content;
    }

    // getters and setters
    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean addToDatabase() {
        String query = "INSERT INTO News (headline, content) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, this.headline);
            stmt.setString(2, this.content);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this.newsId = rs.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding news: " + e.getMessage());
        }
        return false;
    }


    public static List<News> fetchAllNews() {
        List<News> newsList = new ArrayList<>();
        String query = "SELECT * FROM News";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                News news = new News(
                        rs.getInt("news_id"),
                        rs.getString("headline"),
                        rs.getString("content")
                );
                newsList.add(news);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching news: " + e.getMessage());
        }
        return newsList;
    }

    public static News fetchNewsById(int newsId) {
        String query = "SELECT * FROM News WHERE news_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, newsId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new News(
                        rs.getInt("news_id"),
                        rs.getString("headline"),
                        rs.getString("content")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching news by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean modifyInDatabase() {
        String query = "UPDATE News SET headline = ?, content = ? WHERE news_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, this.headline);
            stmt.setString(2, this.content);
            stmt.setInt(3, this.newsId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating news: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteFromDatabase() {
        String query = "DELETE FROM News WHERE news_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, this.newsId);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting news: " + e.getMessage());
        }
        return false;
    }

    @Override
    public String toString() {
        return "News ID: " + newsId + "\nHeadline: " + headline + "\nContent: " + content + "\n";
    }
}
