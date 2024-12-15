package software.developer.bhushan.MoneyPe.db.tables;




import software.developer.bhushan.MoneyPe.db.models.UserAccountModel;

import java.sql.*;
import java.util.ArrayList;

public class UserAccountTable {
    // Column names

    private String Table_Name = "UserAccountTable";

    private String Col_1 = "userId";
    private String Col_2 = "userName";
    private String Col_3 = "userPassword";
    private String Col_4 = "userAccountNumber";
    private String Col_5 = "userDateOfCreation";
    private String Col_6 = "userBalance";

    // Table column definitions
    private String Table_Col_1 = Col_1+ " TEXT PRIMARY KEY,";
    private String Table_Col_2 = Col_2+ " TEXT NOT NULL,";
    private String Table_Col_3 = Col_3 + " TEXT NOT NULL,";
    private String Table_Col_4 = Col_4+ " TEXT NOT NULL,";
    private String Table_Col_5 = Col_5 + " TEXT NOT NULL,";
    private String Table_Col_6 = Col_6 + " REAL NOT NULL";

    // Method to create the table
    public void Create(Connection connection) {
        String query = "CREATE TABLE IF NOT EXISTS "+ Table_Name +" (" +
                Table_Col_1 +
                Table_Col_2 +
                Table_Col_3 +
                Table_Col_4 +
                Table_Col_5 +
                Table_Col_6 + ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            // System.out.println("UserAccount table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating UserAccount table: " + e.getMessage());
        }
    }

    // Method to insert a new user account using UserAccountModel
    public boolean Insert(Connection connection, UserAccountModel userAccount) {
        String query = "INSERT INTO "+ Table_Name +" (" +
                Col_1 + ", " + Col_2 + ", " + Col_3 + ", " + Col_4 + ", " + Col_5 + ", " + Col_6 +
                ") VALUES (?, ?, ?, ?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userAccount.getUserId());
            preparedStatement.setString(2, userAccount.getUserName());
            preparedStatement.setString(3, userAccount.getUserPassword());
            preparedStatement.setString(4, userAccount.getUserAccountNumber());
            preparedStatement.setString(5, userAccount.getUserDateOfCreation());
            preparedStatement.setDouble(6, userAccount.getUserBalance());

            preparedStatement.executeUpdate();
            System.out.println("User Account '"+userAccount.getUserName()+"' Inserted Successfully.");
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting user account: " + e.getMessage());
            return false;
        }
    }

    public UserAccountModel FetchByUserID(Connection connection, String userId) {
        String query = "SELECT * FROM "+ Table_Name +" WHERE " + Col_1 + " = ?";
        UserAccountModel userAccount = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userAccount = new UserAccountModel();
                    userAccount.setUserId(resultSet.getString(Col_1));
                    userAccount.setUserName(resultSet.getString(Col_2));
                    userAccount.setUserPassword(resultSet.getString(Col_3));
                    userAccount.setUserAccountNumber(resultSet.getString(Col_4));
                    userAccount.setUserDateOfCreation(resultSet.getString(Col_5));
                    userAccount.setUserBalance(resultSet.getDouble(Col_6));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user account by userId: " + e.getMessage());
        }

        return userAccount;
    }

    // Method to fetch all UserAccounts and return them as an ArrayList
    public ArrayList<UserAccountModel> FetchUserModelsInArrayList(Connection connection) {
        String query = "SELECT * FROM "+ Table_Name +";";
        ArrayList<UserAccountModel> userList = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                UserAccountModel userAccount = new UserAccountModel();
                userAccount.setUserId(resultSet.getString(Col_1));
                userAccount.setUserName(resultSet.getString(Col_2));
                userAccount.setUserPassword(resultSet.getString(Col_3));
                userAccount.setUserAccountNumber(resultSet.getString(Col_4));
                userAccount.setUserDateOfCreation(resultSet.getString(Col_5));
                userAccount.setUserBalance(resultSet.getDouble(Col_6));

                userList.add(userAccount);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user accounts: " + e.getMessage());
        }

        return userList;
    }

    public UserAccountModel FetchUserAccountModelByUserNameAndPassword(Connection connection, String userName, String userPassword) {
        String query = "SELECT * FROM "+ Table_Name +" WHERE " + Col_2 + " = ? AND " + Col_3 + " = ?";
        UserAccountModel userAccount = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userName);
            statement.setString(2, userPassword);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userAccount = new UserAccountModel();
                userAccount.setUserId(resultSet.getString(Col_1));
                userAccount.setUserName(resultSet.getString(Col_2));
                userAccount.setUserPassword(resultSet.getString(Col_3));
                userAccount.setUserAccountNumber(resultSet.getString(Col_4));
                userAccount.setUserDateOfCreation(resultSet.getString(Col_5));
                userAccount.setUserBalance(resultSet.getDouble(Col_6));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }

        return userAccount;
    }

    public UserAccountModel FetchByAccountNumber(Connection connection, String accountNumber) {
        String query = "SELECT * FROM " + Table_Name + " WHERE " + Col_4 + " = ?";
        UserAccountModel userAccount = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userAccount = new UserAccountModel();
                    userAccount.setUserId(resultSet.getString(Col_1));
                    userAccount.setUserName(resultSet.getString(Col_2));
                    userAccount.setUserPassword(resultSet.getString(Col_3));
                    userAccount.setUserAccountNumber(resultSet.getString(Col_4));
                    userAccount.setUserDateOfCreation(resultSet.getString(Col_5));
                    userAccount.setUserBalance(resultSet.getDouble(Col_6));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by account number: " + e.getMessage());
        }

        return userAccount;
    }

    public void UpdateByUserID(Connection connection, UserAccountModel userAccountModel) {
        // Define the update query with placeholders for values to update
        String query = "UPDATE " + Table_Name + " SET " +
                       Col_2 + " = ?, " + // userName
                       Col_3 + " = ?, " + // userPassword
                       Col_4 + " = ?, " + // userAccountNumber
                       Col_5 + " = ?, " + // userDateOfCreation
                       Col_6 + " = ? " +   // userBalance
                       "WHERE " + Col_1 + " = ?"; // userId
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the parameters in the prepared statement
            preparedStatement.setString(1, userAccountModel.getUserName());
            preparedStatement.setString(2, userAccountModel.getUserPassword());
            preparedStatement.setString(3, userAccountModel.getUserAccountNumber());
            preparedStatement.setString(4, userAccountModel.getUserDateOfCreation());
            preparedStatement.setDouble(5, userAccountModel.getUserBalance());
            preparedStatement.setString(6, userAccountModel.getUserId()); // Set userId for WHERE clause
    
            // Execute the update query
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("User account updated successfully.");
            } else {
                System.out.println("No user found with the given userId.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating user account: " + e.getMessage());
        }
    }

    public void DeleteUserByID(Connection connection, UserAccountModel userAccountModel) {
        // SQL query to delete the user from the UserAccountTable based on userId
        String query = "DELETE FROM " + Table_Name + " WHERE " + Col_1 + " = ?";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the userId from the UserAccountModel to the query parameter
            preparedStatement.setString(1, userAccountModel.getUserId());
    
            // Execute the DELETE query
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("User successfully deleted.");
            } else {
                System.out.println("No user found with the given userId.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    public void DeleteUserByID(Connection connection, String userID) {
        // SQL query to delete the user from the UserAccountTable based on userId
        String query = "DELETE FROM " + Table_Name + " WHERE " + Col_1 + " = ?";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the userId from the UserAccountModel to the query parameter
            preparedStatement.setString(1, userID);
    
            // Execute the DELETE query
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("User successfully deleted.");
            } else {
                System.out.println("No user found with the given userId.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }
    
    
}
