import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Seat {
    private int seatId;
    private int showtimeId;
    private String seatNumber;
    private String status; // "available" or "booked"

    // ctor for existing seats
    public Seat(int seatId, int showtimeId, String seatNumber, String status) {
        this.seatId = seatId;
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    // ctor for new seats (without ID)
    public Seat(int showtimeId, String seatNumber, String status) {
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    // getters and setters
    public int getSeatId() {
        return seatId;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static List<Seat> fetchSeatsByShowtime(int showtimeId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM Seats WHERE showtime_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                seats.add(new Seat(
                        rs.getInt("seat_id"),
                        rs.getInt("showtime_id"),
                        rs.getString("seat_number"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching seats: " + e.getMessage());
        }
        return seats;
    }

    public boolean addSeat() {
        String insertSeat = "INSERT INTO Seats (showtime_id, seat_number, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSeat)) {
            stmt.setInt(1, this.showtimeId);
            stmt.setString(2, this.seatNumber);
            stmt.setString(3, this.status);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding seat: " + e.getMessage());
        }
        return false;
    }

    public boolean modifySeat() {
        String updateSeat = "UPDATE Seats SET seat_number = ?, status = ? WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSeat)) {
            stmt.setString(1, this.seatNumber);
            stmt.setString(2, this.status);
            stmt.setInt(3, this.seatId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error modifying seat: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteSeat() {
        String deleteSeat = "DELETE FROM Seats WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSeat)) {
            stmt.setInt(1, this.seatId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting seat: " + e.getMessage());
        }
        return false;
    }

    public boolean reserveSeat(int i) {
        if (!"Available".equalsIgnoreCase(this.status)) {
            System.out.println("Seat " + this.seatNumber + " is already booked.");
            return false;
        }

        String updateSeat = "UPDATE Seats SET status = 'Booked' WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSeat)) {
            stmt.setInt(1, this.seatId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                this.status = "Booked";
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error reserving seat: " + e.getMessage());
        }
        return false;
    }

    public boolean releaseSeat() {
        if (!"Booked".equalsIgnoreCase(this.status)) {
            System.out.println("Seat " + this.seatNumber + " is already available.");
            return false;
        }

        String updateSeat = "UPDATE Seats SET status = 'Available' WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSeat)) {
            stmt.setInt(1, this.seatId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                this.status = "Available";
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error releasing seat: " + e.getMessage());
        }
        return false;
    }

    // Fetch a specific seat by ID
    public static Seat fetchSeatById(int seatId) {
        String query = "SELECT * FROM Seats WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, seatId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Seat(
                        rs.getInt("seat_id"),
                        rs.getInt("showtime_id"),
                        rs.getString("seat_number"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching seat: " + e.getMessage());
        }
        return null;
    }

    public Object getSeatID() {
        return this.seatId;
    }

}