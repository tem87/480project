//import java.time.LocalDateTime;
//
//public class Ticket {
//    private int ticketID;
//    private Seat seat;
//    private Showtime showtime;
//    private User user;
//    private String status;
//    private Receipt receipt;
//
//    private static int ticketCounter = 1000;
//
//    // Constructors
//    public Ticket(Seat seat, Showtime showtime, User user) {
//        this.ticketID = ticketCounter++;
//        this.seat = seat;
//        this.showtime = showtime;
//        this.user = user;
//        this.status = "active";
//    }
//
//    public Ticket(int ticketID, Seat seat, Showtime showtime, User user, String status) {
//        this.ticketID = ticketID;
//        if (ticketID >= ticketCounter) {
//            ticketCounter = ticketID + 1;
//        }
//        this.seat = seat;
//        this.showtime = showtime;
//        this.user = user;
//        this.status = status;
//    }
//
//    // Getters and Setters
//    public int getTicketID() {
//        return ticketID;
//    }
//
//    public Seat getSeat() {
//        return seat;
//    }
//
//    public void setSeat(Seat seat) {
//        this.seat = seat;
//    }
//
//    public Showtime getShowtime() {
//        return showtime;
//    }
//
//    public void setShowtime(Showtime showtime) {
//        this.showtime = showtime;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public Receipt getReceipt() {
//        return receipt;
//    }
//
//    public void setReceipt(Receipt receipt) {
//        this.receipt = receipt;
//    }
//
//    // Other methods
//    public boolean cancelTicket() {
//        if ("active".equals(this.status)) {
//            this.status = "cancelled";
//            this.seat.releaseSeat();
//            return true;
//        }
//        return false;
//    }
//
//    public String getTicketDetails() {
//        return "Ticket ID: " + ticketID +
//                "\nShowtime: " + showtime.getDateTime() +
//                "\nSeat: " + seat.getSeatNumber() +
//                "\nUser: " + user.getName() +
//                "\nStatus: " + status;
//    }
//}