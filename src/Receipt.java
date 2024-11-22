//import java.time.LocalDateTime;
//
//public class Receipt {
//    private int receiptID;
//    private Payment payment;
//    private Ticket ticket;
//    private User user;
//    private LocalDateTime emailSentDate;
//
//
//    public Receipt(int receiptID, Payment payment, Ticket ticket, User user, LocalDateTime emailSentDate) {
//        this.receiptID = receiptID;
//        this.payment = payment;
//        this.ticket = ticket;
//        this.user = user;
//        this.emailSentDate = emailSentDate;
//    }
//
//    // Getters and Setters
//    public int getReceiptID() {
//        return receiptID;
//    }
//
//    public void setReceiptID(int receiptID) {
//        this.receiptID = receiptID;
//    }
//
//    public Payment getPayment() {
//        return payment;
//    }
//
//    public void setPayment(Payment payment) {
//        this.payment = payment;
//    }
//
//    public Ticket getTicket() {
//        return ticket;
//    }
//
//    public void setTicket(Ticket ticket) {
//        this.ticket = ticket;
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
//    public LocalDateTime getEmailSentDate() {
//        return emailSentDate;
//    }
//
//    public void setEmailSentDate(LocalDateTime emailSentDate) {
//        this.emailSentDate = emailSentDate;
//    }
//    y
//    // Other methods
//    public String generateReceipt() {
//        return "Receipt ID: " + receiptID +
//                "\nUser: " + user.getName() +
//                "\nTicket Details: " + ticket.getTicketDetails() +
//                "\nPayment Amount: " + payment.getAmount() +
//                "\nPayment Date: " + payment.getTransactionDate();
//    }
//
//    public void sendEmail() {
//        System.out.println("Sending email to " + user.getEmail() + " with receipt details...");
//        System.out.println(generateReceipt());
//        emailSentDate = LocalDateTime.now();
//    }
//}