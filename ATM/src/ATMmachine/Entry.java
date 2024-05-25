package ATMmachine;

import java.sql.*;

public class Entry {
	// JDBC URL, username, and password
	static final String JDBC_URL = "jdbc:mysql://localhost:3306/atm_db";
	static final String USERNAME = "root";
	static final String PASSWORD = "(Lsvs1919)";

	public static void main(String[] args) {
		try {
			// Establish connection to the database
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			// Simulate user login or registration
			java.util.Scanner scanner = new java.util.Scanner(System.in);
			System.out.println("Welcome to the ATM System!");
			System.out.println("1. Log in");
			System.out.println("2. Register an account");
			System.out.print("Enter your choice: ");
			int choice = scanner.nextInt();

			if (choice == 1) {
				// User wants to log in
				login(connection);
			} else if (choice == 2) {
				// User wants to register an account
				register(connection);
			} else {
				System.out.println("Invalid choice. Please try again.");
			}

			// Close the connection
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Method for user login
	static void login(Connection connection) {
		try {
			System.out.println("Please enter your username and password to log in.");
			java.util.Scanner scanner = new java.util.Scanner(System.in);
			System.out.print("Username: ");
			String enteredUsername = scanner.nextLine();
			System.out.print("Password: ");
			String enteredPassword = scanner.nextLine();

			// Step 4: Checking User Credentials
			if (checkCredentials(connection, enteredUsername, enteredPassword)) {
				// Step 5: Retrieving User Details and Balance
				double balance = retrieveBalance(connection, enteredUsername);
				System.out.println("Login successful. Welcome, " + enteredUsername + "!");
				System.out.println("Your current balance: $" + balance);

				// Step 6: Displaying Options to the User
				showMenu(connection, enteredUsername);
			} else {
				System.out.println("Invalid username or password. Please try again.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Method for user registration
	static void register(Connection connection) {
		try {
			System.out.println("Please enter a username and password to register.");
			java.util.Scanner scanner = new java.util.Scanner(System.in);
			System.out.print("Username: ");
			String username = scanner.nextLine();
			System.out.print("Password: ");
			String password = scanner.nextLine();

			// Check if the username already exists
			if (checkUsernameExists(connection, username)) {
				System.out.println("Username already exists. Please choose a different one.");
				return;
			}

			// Insert new user into the database
			String insertSql = "INSERT INTO users (username, password, balance) VALUES (?, ?, ?)";
			PreparedStatement insertStatement = connection.prepareStatement(insertSql);
			insertStatement.setString(1, username);
			insertStatement.setString(2, password);
			insertStatement.setDouble(3, 0); // Initial balance is 0
			insertStatement.executeUpdate();

			System.out.println("Registration successful. You can now log in with your new account.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Step 4: Checking User Credentials
	static boolean checkCredentials(Connection connection, String username, String password) throws SQLException {
		String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, username);
		statement.setString(2, password);
		ResultSet resultSet = statement.executeQuery();
		return resultSet.next(); // Returns true if at least one row is returned
	}

	// Step 5: Retrieving User Details and Balance
	static double retrieveBalance(Connection connection, String username) throws SQLException {
		String sql = "SELECT balance FROM users WHERE username = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, username);
		ResultSet resultSet = statement.executeQuery();
		if (resultSet.next()) {
			return resultSet.getDouble("balance");
		}
		return 0; // Return 0 if user not found (you can handle this differently based on your
					// requirements)
	}

	// Step 6: Displaying Options to the User
	static void showMenu(Connection connection, String username) {
		try {
			while (true) {
				System.out.println("\nChoose an option:");
				System.out.println("1. Check Balance");
				System.out.println("2. Deposit Money");
				System.out.println("3. Withdraw Money");
				System.out.println("4. Exit");
				System.out.print("Enter your choice: ");

				// Read user choice
				java.util.Scanner scanner = new java.util.Scanner(System.in);
				int choice = scanner.nextInt();

				switch (choice) {
				case 1:
					double balance = retrieveBalance(connection, username);
					System.out.println("Your current balance: $" + balance);
					break;
				case 2:
					System.out.print("Enter amount to deposit: $");
					double depositAmount = scanner.nextDouble();
					deposit(connection, username, depositAmount);
					break;
				case 3:
					System.out.print("Enter amount to withdraw: $");
					double withdrawAmount = scanner.nextDouble();
					withdraw(connection, username, withdrawAmount);
					break;
				case 4:
					System.out.println("Thank you for using our ATM. Goodbye!");
					return;
				default:
					System.out.println("Invalid choice. Please enter a number between 1 and 4.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Method to deposit money into the user's account
	static void deposit(Connection connection, String username, double amount) {
		try {
			double currentBalance = retrieveBalance(connection, username);
			double newBalance = currentBalance + amount;

			// Update balance in the database
			String updateSql = "UPDATE users SET balance = ? WHERE username = ?";
			PreparedStatement updateStatement = connection.prepareStatement(updateSql);
			updateStatement.setDouble(1, newBalance);
			updateStatement.setString(2, username);
			updateStatement.executeUpdate();

			System.out.println("Deposit successful. Your new balance: $" + newBalance);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Method to withdraw money from the user's account
	static void withdraw(Connection connection, String username, double amount) {
		try {
			double currentBalance = retrieveBalance(connection, username);
			if (currentBalance >= amount) {
				double newBalance = currentBalance - amount;

				// Update balance in the database
				String updateSql = "UPDATE users SET balance = ? WHERE username = ?";
				PreparedStatement updateStatement = connection.prepareStatement(updateSql);
				updateStatement.setDouble(1, newBalance);
				updateStatement.setString(2, username);
				updateStatement.executeUpdate();

				System.out.println("Withdrawal successful. Your new balance: $" + newBalance);
			} else {
				System.out.println("Insufficient balance. Please enter a lower amount.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Method to check if the username already exists in the database
	static boolean checkUsernameExists(Connection connection, String username) throws SQLException {
		String sql = "SELECT * FROM users WHERE username = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, username);
		ResultSet resultSet = statement.executeQuery();
		return resultSet.next(); // Returns true if at least one row is returned
	}
}
