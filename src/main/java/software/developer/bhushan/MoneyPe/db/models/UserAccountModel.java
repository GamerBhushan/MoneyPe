package software.developer.bhushan.MoneyPe.db.models;

public class UserAccountModel {
    private String userId, userName, userPassword, userAccountNumber, userDateOfCreation;
    private double userBalance;

    public UserAccountModel(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserAccountNumber() {
        return userAccountNumber;
    }

    public void setUserAccountNumber(String userAccountNumber) {
        this.userAccountNumber = userAccountNumber;
    }

    public String getUserDateOfCreation() {
        return userDateOfCreation;
    }

    public void setUserDateOfCreation(String userDateOfCreation) {
        this.userDateOfCreation = userDateOfCreation;
    }

    public double getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(double userBalance) {
        this.userBalance = userBalance;
    }
}
