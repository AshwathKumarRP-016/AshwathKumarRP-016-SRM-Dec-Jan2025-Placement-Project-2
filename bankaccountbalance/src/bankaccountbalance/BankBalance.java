package bankaccountbalance;

import java.sql.*;
import java.sql.DriverManager;
import java.util.Scanner;


public class BankBalance {

	public static void main(String[] args) {
		
		        Scanner scanner = new Scanner(System.in);
		        
		        // Database connection 
		        String url = "jdbc:mysql://localhost:3306/bank_system";
		        String username = "root";
		        String password = "root"; 
		        
		        Connection connection = null;
		        
		        System.out.println("==================================");
		        System.out.println("          BANK SYSTEM             ");
		        System.out.println("==================================");
		        
		        try {
		            Class.forName("com.mysql.cj.jdbc.Driver");
		           
		            connection = DriverManager.getConnection(url, username, password);
		            
		            
		           
		            while (true) {
		                System.out.println("\n======== MAIN MENU ========\n");
		                System.out.println("1. Create New Account");
		                System.out.println("2. Deposit Money");
		                System.out.println("3. Check Balance");
		                System.out.println("4. Exit");
		                System.out.print("Enter your choice (1-4): ");
		                
		                int choice = scanner.nextInt();
		                scanner.nextLine(); 
		                
		                switch (choice) {
		                    case 1:
		                        createAccount(connection, scanner);
		                        break;
		                    case 2:
		                        depositMoney(connection, scanner);
		                        break;
		                    case 3:
		                        checkBalance(connection, scanner);
		                        break;
		                    case 4:
		                        System.out.println("Thank you for using our bank!");
		                        scanner.close();
		                        if (connection != null) connection.close();
		                        return;
		                    default:
		                        System.out.println("Invalid choice! Please enter 1-4.");
		                }
		            }
		            
		        } catch (ClassNotFoundException e) {
		            System.out.println(" Error: MySQL Driver not found!");
		        } catch (SQLException e) {
		            System.out.println(" Database error: " + e.getMessage());
		        } finally {
		            try {
		                if (connection != null) connection.close();
		            } catch (SQLException e) {
		                System.out.println("Error closing connection: " + e.getMessage());
		            }
		        }
		    }
		    
		    
		    // 1. CREATE NEW ACCOUNT
		    
		    private static void createAccount(Connection connection, Scanner scanner) throws SQLException {
		        System.out.println("\n--- CREATE NEW ACCOUNT ---\n");
		  
		        System.out.print("Enter your full name: ");
		        String name = scanner.nextLine();
		        
		        
		        double initialDeposit = 0;
		        while (true) {
		            System.out.print("Enter initial deposit amount: ₹");
		            try {
		                initialDeposit = scanner.nextDouble();
		                scanner.nextLine(); 
		                
		                if (initialDeposit < 0) {
		                    System.out.println(" Deposit amount cannot be negative!");
		                } else {
		                    break;
		                }
		            } catch (Exception e) {
		                System.out.println(" Invalid amount! Please enter numbers only.");
		                scanner.nextLine(); 
		            }
		        }
		        
		        
		        String sql = "INSERT INTO accounts (holder_name, balance) VALUES (?, ?)";
		        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		        pstmt.setString(1, name);
		        pstmt.setDouble(2, initialDeposit);
		        
		        int rowsInserted = pstmt.executeUpdate();
		        
		        if (rowsInserted > 0) {
		            
		            ResultSet generatedKeys = pstmt.getGeneratedKeys();
		            if (generatedKeys.next()) {
		                int accountNumber = generatedKeys.getInt(1);
		                System.out.println("\n ACCOUNT CREATED SUCCESSFULLY!\n");
		                System.out.println("--------------------------------");
		                System.out.println("Account Number: " + accountNumber);
		                System.out.println("Account Holder: " + name);
		                System.out.println("Initial Balance: ₹" + initialDeposit);
		                System.out.println("--------------------------------");
		                System.out.println(" !!Please remember your Account Number!!");
		            }
		            generatedKeys.close();
		        }
		        
		        pstmt.close();
		    }
		    
		   
		    // 2. DEPOSIT MONEY
		   
		    private static void depositMoney(Connection connection, Scanner scanner) throws SQLException {
		        System.out.println("\n--- DEPOSIT MONEY ---\n");
		        
		        
		        System.out.print("Enter your account number: ");
		        int accountNumber = scanner.nextInt();
		        scanner.nextLine(); 
		        
		        
		        if (!accountExists(connection, accountNumber)) {
		            System.out.println(" Account not found! Please check the account number.");
		            return;
		        }
		        
		        String holderName = getAccountHolderName(connection, accountNumber);
		        System.out.println("Account Holder: " + holderName);
		        
		       
		        double currentBalance = getBalance(connection, accountNumber);
		        System.out.println("Current Balance: ₹" + currentBalance);
		        
		        
		        double depositAmount = 0;
		        while (true) {
		            System.out.print("Enter amount to deposit: ₹");
		            try {
		                depositAmount = scanner.nextDouble();
		                scanner.nextLine(); 
		                
		                if (depositAmount <= 0) {
		                    System.out.println(" Deposit amount must be positive!");
		                } else {
		                    break;
		                }
		            } catch (Exception e) {
		                System.out.println(" Invalid amount! Please enter numbers only.");
		                scanner.nextLine(); 
		            }
		        }
		        
		        
		        double newBalance = currentBalance + depositAmount;
		        
		        
		        String updateSQL = "UPDATE accounts SET balance = ? WHERE acc_no = ?";
		        PreparedStatement updateStmt = connection.prepareStatement(updateSQL);
		        updateStmt.setDouble(1, newBalance);
		        updateStmt.setInt(2, accountNumber);
		        
		        int rowsUpdated = updateStmt.executeUpdate();
		        updateStmt.close();
		        
		        if (rowsUpdated > 0) {
		            System.out.println("\nDEPOSIT SUCCESSFUL!\n");
		            System.out.println("--------------------------------");
		            System.out.println("Account Number: " + accountNumber);
		            System.out.println("Account Holder: " + holderName);
		            System.out.println("Deposit Amount: ₹" + depositAmount);
		            System.out.println("Previous Balance: ₹" + currentBalance);
		            System.out.println("New Balance: ₹" + newBalance);
		            System.out.println("--------------------------------");
		        }
		    }
		    
		    
		    // 3. CHECK BALANCE
		   
		    private static void checkBalance(Connection connection, Scanner scanner) throws SQLException {
		        System.out.println("\n--- CHECK BALANCE ---");
		        
		       
		        System.out.print("Enter your account number: ");
		        int accountNumber = scanner.nextInt();
		        scanner.nextLine(); 
		        
		       
		        if (!accountExists(connection, accountNumber)) {
		            System.out.println("❌ Account not found! Please check the account number.");
		            return;
		        }
		        
		        
		        String sql = "SELECT * FROM accounts WHERE acc_no = ?";
		        PreparedStatement pstmt = connection.prepareStatement(sql);
		        pstmt.setInt(1, accountNumber);
		        ResultSet rs = pstmt.executeQuery();
		        
		        if (rs.next()) {
		            System.out.println("\n--- ACCOUNT DETAILS ---");
		            System.out.println("Account Number: " + accountNumber);
		            System.out.println("Account Holder: " + rs.getString("holder_name"));
		            System.out.println("Current Balance: ₹" + rs.getDouble("balance"));
		            System.out.println("Account Created: " + rs.getTimestamp("created_date"));
		            System.out.println("-----------------------");
		            
		            
		            double balance = rs.getDouble("balance");
		            if (balance > 100000) {
		                System.out.println("You have a healthy balance!");
		            } else if (balance < 1000) {
		                System.out.println("Your balance is low. Consider depositing money.");
		            }
		        }
		        
		        rs.close();
		        pstmt.close();
		    }
		    
		    
		    // Tools
		    
		    
		    private static boolean accountExists(Connection connection, int accountNumber) throws SQLException {
		        String sql = "SELECT COUNT(*) as count FROM accounts WHERE acc_no = ?";
		        PreparedStatement pstmt = connection.prepareStatement(sql);
		        pstmt.setInt(1, accountNumber);
		        ResultSet rs = pstmt.executeQuery();
		        
		        boolean exists = false;
		        if (rs.next() && rs.getInt("count") > 0) {
		            exists = true;
		        }
		        
		        rs.close();
		        pstmt.close();
		        return exists;
		    }
		    
		    
		    private static String getAccountHolderName(Connection connection, int accountNumber) throws SQLException {
		        String sql = "SELECT holder_name FROM accounts WHERE acc_no = ?";
		        PreparedStatement pstmt = connection.prepareStatement(sql);
		        pstmt.setInt(1, accountNumber);
		        ResultSet rs = pstmt.executeQuery();
		        
		        String name = "";
		        if (rs.next()) {
		            name = rs.getString("holder_name");
		        }
		        
		        rs.close();
		        pstmt.close();
		        return name;
		    }
		    
		    
		    private static double getBalance(Connection connection, int accountNumber) throws SQLException {
		        String sql = "SELECT balance FROM accounts WHERE acc_no = ?";
		        PreparedStatement pstmt = connection.prepareStatement(sql);
		        pstmt.setInt(1, accountNumber);
		        ResultSet rs = pstmt.executeQuery();
		        
		        double balance = 0;
		        if (rs.next()) {
		            balance = rs.getDouble("balance");
		        }
		        
		        rs.close();
		        pstmt.close();
		        return balance;
		    }
		}
	


