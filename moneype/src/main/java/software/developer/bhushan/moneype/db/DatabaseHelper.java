package software.developer.bhushan.moneype.db;

import software.developer.bhushan.moneype.Paths;
import software.developer.bhushan.moneype.db.tables.UserAccountTable;
import software.developer.bhushan.moneype.db.tables.MoneyTransactionTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    private static String DB_MONEY_PAY = "MoneyPe.db";
    private static String JDBC_SQLITE = "jdbc:sqlite:";

    private static boolean isOnCreateCalled = false;

    private Connection connection;

    private UserAccountTable userAccountTable = new UserAccountTable();
    private MoneyTransactionTable moneyTransactionTable = new MoneyTransactionTable();

    private String url = JDBC_SQLITE+ Paths.RESOURCES_FOLDER_PATH+DB_MONEY_PAY;
    public DatabaseHelper(){
        // Explicitly load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
            initConnection();
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        }
    }

    private void initConnection(){
        try{
            connection = DriverManager.getConnection(url);
            if (!isOnCreateCalled){
                onCreate();
            }
        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void onCreate(){
        userAccountTable.Create(connection);
        moneyTransactionTable.Create(connection);
        isOnCreateCalled = true;
    }

    public Connection getConnection() {
        return connection;
    }

    public UserAccountTable getUserAccountTable() {
        return userAccountTable;
    }

    public MoneyTransactionTable getMoneyTransactionTable() {
        return moneyTransactionTable;
    }
}
