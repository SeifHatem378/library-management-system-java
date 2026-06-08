import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String DB_URL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=library_db;integratedSecurity=true;trustServerCertificate=true;";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Advanced Library Management System ===");
            System.out.println("1. Add a New User");
            System.out.println("2. Add a New Book");
            System.out.println("3. View All Books");
            System.out.println("4. Borrow a Book");
            System.out.println("5. Return a Book");
            System.out.println("6. View Borrowing History");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addUser();
                    break;
                case 2:
                    addBook();
                    break;
                case 3:
                    viewBooks();
                    break;
                case 4:
                    borrowBook();
                    break;
                case 5:
                    returnBook();
                    break;
                case 6:
                    viewBorrowings();
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addUser() {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        System.out.print("Enter Role (admin/user): ");
        String role = scanner.nextLine();

        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, role);
            
            pstmt.executeUpdate();
            System.out.println("Success: User added!");
        } catch (SQLException e) {
            System.out.println("Error adding user.");
        }
    }

    private static void addBook() {
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter Author Name: ");
        String author = scanner.nextLine();
        
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        
        System.out.print("Enter Available Count: ");
        int count = scanner.nextInt();

        String sql = "INSERT INTO books (title, author, category, available_count) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, category);
            pstmt.setInt(4, count);
            
            pstmt.executeUpdate();
            System.out.println("Success: Book added!");
        } catch (SQLException e) {
            System.out.println("Error adding book.");
        }
    }

    private static void viewBooks() {
        String sql = "SELECT * FROM books";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- Books List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                                   " | Title: " + rs.getString("title") + 
                                   " | Category: " + rs.getString("category") +
                                   " | Available: " + rs.getInt("available_count"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books.");
        }
    }

    private static void borrowBook() {
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();
        
        System.out.print("Enter Book ID: ");
        int bookId = scanner.nextInt();

        String checkSql = "SELECT available_count FROM books WHERE id = ?";
        String borrowSql = "INSERT INTO borrowings (user_id, book_id) VALUES (?, ?)";
        String updateBookSql = "UPDATE books SET available_count = available_count - 1 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement borrowStmt = conn.prepareStatement(borrowSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateBookSql)) {

            checkStmt.setInt(1, bookId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt("available_count") > 0) {
                    
                    borrowStmt.setInt(1, userId);
                    borrowStmt.setInt(2, bookId);
                    borrowStmt.executeUpdate();

                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();

                    System.out.println("Success: Book borrowed!");
                } else {
                    System.out.println("Error: Book is out of stock or does not exist.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during borrowing process.");
        }
    }

    private static void returnBook() {
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();
        
        System.out.print("Enter Book ID: ");
        int bookId = scanner.nextInt();

        String returnSql = "UPDATE borrowings SET return_date = GETDATE(), status = 'returned' WHERE user_id = ? AND book_id = ? AND status = 'borrowed'";
        String updateBookSql = "UPDATE books SET available_count = available_count + 1 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement returnStmt = conn.prepareStatement(returnSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateBookSql)) {

            returnStmt.setInt(1, userId);
            returnStmt.setInt(2, bookId);
            int rowsAffected = returnStmt.executeUpdate();

            if (rowsAffected > 0) {
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
                System.out.println("Success: Book returned!");
            } else {
                System.out.println("Error: No active borrowing record found.");
            }
        } catch (SQLException e) {
            System.out.println("Error during returning process.");
        }
    }

    private static void viewBorrowings() {
        String sql = "SELECT * FROM borrowings";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- Borrowing History ---");
            while (rs.next()) {
                System.out.println("Borrow ID: " + rs.getInt("id") + 
                                   " | User ID: " + rs.getInt("user_id") + 
                                   " | Book ID: " + rs.getInt("book_id") +
                                   " | Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving history.");
        }
    }
}