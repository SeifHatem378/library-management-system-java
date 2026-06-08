import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String DB_URL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=library_db;integratedSecurity=true;trustServerCertificate=true;";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Advanced Library System ===");
            System.out.println("1. Add a New Book");
            System.out.println("2. Add a New Member");
            System.out.println("3. View All Books");
            System.out.println("4. View All Members");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    addMember();
                    break;
                case 3:
                    viewBooks();
                    break;
                case 4:
                    viewMembers();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addBook() {
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter Author Name: ");
        String author = scanner.nextLine();

        String sql = "INSERT INTO books (title, author) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            
            pstmt.executeUpdate();
            System.out.println("Success: Book added to the database!");
        } catch (SQLException e) {
            System.out.println("Error adding the book.");
        }
    }

    private static void addMember() {
        System.out.print("Enter Member Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();

        String sql = "INSERT INTO members (name, phone) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            
            pstmt.executeUpdate();
            System.out.println("Success: Member added to the database!");
        } catch (SQLException e) {
            System.out.println("Error adding the member.");
        }
    }

    private static void viewBooks() {
        String sql = "SELECT * FROM books";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- Books List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("book_id") + 
                                   " | Title: " + rs.getString("title") + 
                                   " | Author: " + rs.getString("author") +
                                   " | Available: " + rs.getBoolean("is_available"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books.");
        }
    }

    private static void viewMembers() {
        String sql = "SELECT * FROM members";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- Members List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("member_id") + 
                                   " | Name: " + rs.getString("name") + 
                                   " | Phone: " + rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving members.");
        }
    }
}