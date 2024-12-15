package software.developer.bhushan.MoneyPe.db.tables;

import software.developer.bhushan.MoneyPe.db.models.MoneyTransactionModel;

import java.sql.*;
import java.util.ArrayList;

public class MoneyTransactionTable {
    private String Table_Name = "MoneyTransactionTable";
    private String Col_1 = "transactionID";
    private String Col_2 = "transactionDate";
    private String Col_3 = "transactionTime";
    private String Col_4 = "transferFrom";
    private String Col_5 = "transferTo";
    private String Col_6 = "moneyAmount";

    // Table column definitions
    private String Table_Col_1 = Col_1 + " TEXT PRIMARY KEY,";
    private String Table_Col_2 = Col_2 + " TEXT NOT NULL,";
    private String Table_Col_3 = Col_3 + " TEXT NOT NULL,";
    private String Table_Col_4 = Col_4 + " TEXT NOT NULL,";
    private String Table_Col_5 = Col_5 + " TEXT NOT NULL,";
    private String Table_Col_6 = Col_6 + " REAL NOT NULL";

    // Method to create the MoneyTransaction table
    public void Create(Connection connection) {
        String query = "CREATE TABLE IF NOT EXISTS "+ Table_Name +" (" +
                Table_Col_1 +
                Table_Col_2 +
                Table_Col_3 +
                Table_Col_4 +
                Table_Col_5 +
                Table_Col_6 +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            // System.out.println("MoneyTransaction table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating MoneyTransaction table: " + e.getMessage());
        }
    }

    // Method to insert a new money transaction using MoneyTransactionModel
    public boolean Insert(Connection connection, MoneyTransactionModel transaction) {
        String query = "INSERT INTO "+ Table_Name +" (" +
                Col_1 + ", " + Col_2 + ", " + Col_3 + ", " + Col_4 + ", " + Col_5 + ", " + Col_6 +
                ") VALUES (?, ?, ?, ?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, transaction.getTransactionID());
            preparedStatement.setString(2, transaction.getTransactionDate());
            preparedStatement.setString(3, transaction.getTransactionTime());
            preparedStatement.setString(4, transaction.getTransferFrom());
            preparedStatement.setString(5, transaction.getTransferTo());
            preparedStatement.setDouble(6, transaction.getMoneyAmount());

            preparedStatement.executeUpdate();
            // System.out.println("Money Transaction "+ transaction.getTransactionID()+" Inserted Successfully.");
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting money transaction: " + e.getMessage());
            return false;
        }
    }

    public MoneyTransactionModel FetchByTransactionID(Connection connection, String transactionID) {
        String query = "SELECT * FROM "+ Table_Name +" WHERE transactionID = ?";
        MoneyTransactionModel transaction = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, transactionID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    transaction = new MoneyTransactionModel();
                    transaction.setTransactionID(resultSet.getString(Col_1));
                    transaction.setTransactionDate(resultSet.getString(Col_2));
                    transaction.setTransactionTime(resultSet.getString("transactionTime"));
                    transaction.setTransferFrom(resultSet.getString("transferFrom"));
                    transaction.setTransferTo(resultSet.getString("transferTo"));
                    transaction.setMoneyAmount(resultSet.getDouble("moneyAmount"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transaction by ID: " + e.getMessage());
        }

        return transaction;
    }

    public ArrayList<MoneyTransactionModel> FetchByTransferFromOrTransferTo(Connection connection, String transferFromOrTo) {
        // Update the query to search for either transferFrom or transferTo
        String query = "SELECT * FROM " + Table_Name + " WHERE " + Col_4 + " = ? OR " + Col_5 + " = ?";
        ArrayList<MoneyTransactionModel> transactions = new ArrayList<>();
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set both parameters for transferFrom and transferTo
            preparedStatement.setString(1, transferFromOrTo);
            preparedStatement.setString(2, transferFromOrTo);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    MoneyTransactionModel transaction = new MoneyTransactionModel();
                    transaction.setTransactionID(resultSet.getString(Col_1));
                    transaction.setTransactionDate(resultSet.getString(Col_2));
                    transaction.setTransactionTime(resultSet.getString(Col_3));
                    transaction.setTransferFrom(resultSet.getString(Col_4));
                    transaction.setTransferTo(resultSet.getString(Col_5));
                    transaction.setMoneyAmount(resultSet.getDouble(Col_6));
    
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transactions by transferFrom or transferTo: " + e.getMessage());
        }
    
        return transactions;
    }
    
    public void DeleteByTransferFromORTransferTo(Connection connection, String transferFromTo) {
        // Define the DELETE query with OR condition for transferFrom or transferTo
        String query = "DELETE FROM " + Table_Name + " WHERE " +
                       Col_4 + " = ? OR " + Col_5 + " = ?";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the parameter for both transferFrom and transferTo
            preparedStatement.setString(1, transferFromTo);
            preparedStatement.setString(2, transferFromTo);
    
            // Execute the DELETE query
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Transactions successfully deleted.");
            } else {
                System.out.println("No transactions found for the given transferFrom or transferTo.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting transactions: " + e.getMessage());
        }
    }
    

}
