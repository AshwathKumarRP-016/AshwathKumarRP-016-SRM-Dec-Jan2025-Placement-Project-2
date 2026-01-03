import java.sql.*;
import java.util.Scanner;

public class BankAccountBalanceChecker {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = null;
        
        try {
            // 1. Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC Driver loaded successfully!");
            
            // 2. Establish database connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database successfully!");
            
            // 3. Create accounts table if it doesn't exist
            createAccountsTable(connection);
            
            // 4. Menu for user interaction
            boolean exit = false;
            while (!exit) {
                System.out.println("\n=== Bank Account Balance Checker ===");
                System.out.println("1. Check Account Balance");
                System.out.println("2. Add Sample Accounts");
                System.out.println("3. View All Accounts");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        checkAccountBalance(connection, scanner);
                        break;
                    case 2:
                        addSampleAccounts(connection);
                        break;
                    case 3:
                        viewAllAccounts(connection);
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Thank you for using Bank Account Balance Checker!");
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection error!");
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            scanner.close();
        }
    }
    
    /**
     * Creates the accounts table if it doesn't exist
     */
    private static void createAccountsTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts (" +
                                "acc_no INT PRIMARY KEY, " +
                                "holder_name VARCHAR(50) NOT NULL, " +
                                "balance DOUBLE NOT NULL)";
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Accounts table ready!");
        }
    }
    
    /**
     * Checks account balance for a given account number
     */
    private static void checkAccountBalance(Connection connection, Scanner scanner) {
        System.out.print("\nEnter account number: ");
        int accountNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        String selectSQL = "SELECT holder_name, balance FROM accounts WHERE acc_no = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            // Set parameter for PreparedStatement
            preparedStatement.setInt(1, accountNumber);
            
            // Execute query and process ResultSet
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Account found - retrieve data from ResultSet
                    String holderName = resultSet.getString("holder_name");
                    double balance = resultSet.getDouble("balance");
                    
                    System.out.println("\n=== Account Details ===");
                    System.out.println("Account Number: " + accountNumber);
                    System.out.println("Account Holder: " + holderName);
                    System.out.println("Current Balance: $" + String.format("%.2f", balance));
                } else {
                    // Account not found
                    System.out.println("Account number " + accountNumber + " not found!");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Adds sample accounts to the database for testing
     */
    private static void addSampleAccounts(Connection connection) throws SQLException {
        String insertSQL = "INSERT INTO accounts (acc_no, holder_name, balance) VALUES (?, ?, ?) " +
                          "ON DUPLICATE KEY UPDATE holder_name = VALUES(holder_name), balance = VALUES(balance)";
        
        // Sample account data
        Object[][] sampleAccounts = {
            {1001, "John Smith", 2500.75},
            {1002, "Alice Johnson", 15000.50},
            {1003, "Robert Brown", 750.25},
            {1004, "Emma Wilson", 3500.00},
            {1005, "David Miller", 12000.80}
        };
        
        int rowsInserted = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            for (Object[] account : sampleAccounts) {
                preparedStatement.setInt(1, (int) account[0]);
                preparedStatement.setString(2, (String) account[1]);
                preparedStatement.setDouble(3, (double) account[2]);
                preparedStatement.addBatch();
            }
            
            int[] results = preparedStatement.executeBatch();
            rowsInserted = results.length;
        }
        
        System.out.println(rowsInserted + " sample accounts added/updated successfully!");
    }
    
    /**
     * Displays all accounts from the database
     */
    private static void viewAllAccounts(Connection connection) {
        String selectSQL = "SELECT * FROM accounts ORDER BY acc_no";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            
            System.out.println("\n=== All Bank Accounts ===");
            System.out.printf("%-15s %-20s %-15s%n", "Account No.", "Holder Name", "Balance");
            System.out.println("----------------------------------------------------");
            
            boolean hasAccounts = false;
            while (resultSet.next()) {
                hasAccounts = true;
                int accNo = resultSet.getInt("acc_no");
                String holderName = resultSet.getString("holder_name");
                double balance = resultSet.getDouble("balance");
                
                System.out.printf("%-15d %-20s $%-14.2f%n", accNo, holderName, balance);
            }
            
            if (!hasAccounts) {
                System.out.println("No accounts found in the database!");
                System.out.println("Please add sample accounts first.");
            }
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
