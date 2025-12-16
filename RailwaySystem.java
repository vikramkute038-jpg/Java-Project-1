import java.sql.*;
import java.util.Scanner;


class Train {
    int trainNo;
    String name;
    String source;
    String destination;
    int seats;
    double fare;

    Train(int trainNo, String name, String source, String destination, int seats, double fare) {
        this.trainNo = trainNo;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.seats = seats;
        this.fare = fare;
    }
}


class Ticket {
    int ticketId;
    String passengerName;
    Train train;

    Ticket(int ticketId, String passengerName, Train train) {
        this.ticketId = ticketId;
        this.passengerName = passengerName;
        this.train = train;
    }
}

public class RailwaySystem {
    static final String DB_URL = "jdbc:mysql://localhost:3306/railway_station";
    static final String USER = "root";
    static final String PASS = "Vikram@038"; 

    static Scanner sc = new Scanner(System.in);

    
    public static Connection getConnection() {
        try {
    
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            return null;
        }
    }

    
    public static void addDefaultTrains() {
        try (Connection conn = getConnection()) {
            if (conn == null) return;

            
            String createTrainTable = "CREATE TABLE IF NOT EXISTS trains (" +
                    "train_no INT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "source VARCHAR(50), " +
                    "destination VARCHAR(50), " +
                    "seats INT, " +
                    "fare DOUBLE)";
            
            String createTicketTable = "CREATE TABLE IF NOT EXISTS tickets (" +
                    "ticket_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "passenger_name VARCHAR(100), " +
                    "train_no INT, " +
                    "FOREIGN KEY (train_no) REFERENCES trains(train_no))";

            Statement stmt = conn.createStatement();
            stmt.execute(createTrainTable);
            stmt.execute(createTicketTable);

            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM trains");
            rs.next();
            if (rs.getInt(1) == 0) {
                String insertSQL = "INSERT INTO trains VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(insertSQL);

                Object[][] trainsData = {
                    {101, "Rajdhani Express", "Mumbai", "Delhi", 50, 1200.0},
                    {202, "Shatabdi Express", "Pune", "Nagpur", 60, 850.0},
                    {303, "Duronto Express", "Delhi", "Kolkata", 40, 1500.0},
                    {404, "Garib Rath", "Mumbai", "Jaipur", 80, 600.0}
                };

                for (Object[] t : trainsData) {
                    pstmt.setInt(1, (int) t[0]);
                    pstmt.setString(2, (String) t[1]);
                    pstmt.setString(3, (String) t[2]);
                    pstmt.setString(4, (String) t[3]);
                    pstmt.setInt(5, (int) t[4]);
                    pstmt.setDouble(6, (double) t[5]);
                    pstmt.executeUpdate();
                }
                System.out.println("Default trains added to database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static void showTrains() {
        System.out.println("\n--- Available Trains ---");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM trains")) {
            
            if (conn == null) return;

            while (rs.next()) {
                System.out.println(rs.getInt("train_no") + " - " + rs.getString("name") + 
                                   " (" + rs.getString("source") + " → " + rs.getString("destination") + 
                                   "), Seats: " + rs.getInt("seats") + ", Fare: Rs." + rs.getDouble("fare"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
    }


    public static void bookTicket() {
        System.out.print("Enter Train Number: ");
        int tno = sc.nextInt();
        
        try (Connection conn = getConnection()) {
            if (conn == null) return;

            
            String checkSQL = "SELECT * FROM trains WHERE train_no = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, tno);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Invalid Train Number!\n");
                return;
            }

            int seats = rs.getInt("seats");
            if (seats <= 0) {
                System.out.println("No seats available!\n");
                return;
            }

            System.out.print("Enter Passenger Name: ");
            String name = sc.next();

            
            conn.setAutoCommit(false);

            try {
                String bookSQL = "INSERT INTO tickets (passenger_name, train_no) VALUES (?, ?)";
                PreparedStatement bookStmt = conn.prepareStatement(bookSQL, Statement.RETURN_GENERATED_KEYS);
                bookStmt.setString(1, name);
                bookStmt.setInt(2, tno);
                bookStmt.executeUpdate();

                ResultSet keys = bookStmt.getGeneratedKeys();
                int ticketId = -1;
                if (keys.next()) {
                    ticketId = keys.getInt(1);
                }

                String updateSeatsSQL = "UPDATE trains SET seats = seats - 1 WHERE train_no = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSeatsSQL);
                updateStmt.setInt(1, tno);
                updateStmt.executeUpdate();

                conn.commit(); 
                System.out.println("Ticket Booked Successfully! Ticket ID: " + ticketId + "\n");

            } catch (SQLException e) {
                conn.rollback(); 
                System.out.println("Booking Failed: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
    public static void cancelTicket() {
        System.out.print("Enter Ticket ID to Cancel: ");
        int id = sc.nextInt();

        try (Connection conn = getConnection()) {
            if (conn == null) return;

            
            String getTicketSQL = "SELECT train_no FROM tickets WHERE ticket_id = ?";
            PreparedStatement getStmt = conn.prepareStatement(getTicketSQL);
            getStmt.setInt(1, id);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                int trainNo = rs.getInt("train_no");

                conn.setAutoCommit(false);

                try {
                    String deleteSQL = "DELETE FROM tickets WHERE ticket_id = ?";
                    PreparedStatement delStmt = conn.prepareStatement(deleteSQL);
                    delStmt.setInt(1, id);
                    delStmt.executeUpdate();

                    String updateSeatsSQL = "UPDATE trains SET seats = seats + 1 WHERE train_no = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSeatsSQL);
                    updateStmt.setInt(1, trainNo);
                    updateStmt.executeUpdate();

                    conn.commit();
                    System.out.println("Ticket Cancelled Successfully!\n");

                } catch (SQLException e) {
                    conn.rollback();
                    System.out.println("Cancellation Failed: " + e.getMessage());
                }
            } else {
                System.out.println("Ticket Not Found!\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static void viewTickets() {
        System.out.println("\n--- Booked Tickets ---");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT t.ticket_id, t.passenger_name, tr.name, tr.source, tr.destination " +
                 "FROM tickets t JOIN trains tr ON t.train_no = tr.train_no")) {
            
            if (conn == null) return;

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Ticket ID: " + rs.getInt("ticket_id") +
                                   ", Name: " + rs.getString("passenger_name") +
                                   ", Train: " + rs.getString("name") +
                                   " (" + rs.getString("source") + " → " + rs.getString("destination") + ")");
            }
            
            if (!found) {
                System.out.println("No Tickets Booked Yet.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    
    public static void main(String[] args) {
        addDefaultTrains();

        while (true) {
            System.out.println("=== Railway Ticket Booking System ===");
            System.out.println("1. View Trains");
            System.out.println("2. Book Ticket");
            System.out.println("3. Cancel Ticket");
            System.out.println("4. View Booked Tickets");
            System.out.println("5. Exit");
            System.out.print("Enter Choice: ");

        
            if (!sc.hasNextInt()) {
                System.out.println("Invalid Input! Please enter a number.\n");
                sc.next(); 
                continue;
            }

            int ch = sc.nextInt(); 

            switch (ch) {
                case 1: showTrains(); break;
                case 2: bookTicket(); break;
                case 3: cancelTicket(); break;
                case 4: viewTickets(); break;
                case 5:
                    System.out.println("Thank you for using Railway System!");
                    return;
                default:
                    System.out.println("Invalid Choice!\n");
            }
        }
    }
}

