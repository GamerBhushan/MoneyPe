package software.developer.bhushan.MoneyPe;


import software.developer.bhushan.MoneyPe.db.DatabaseHelper;
import software.developer.bhushan.MoneyPe.db.models.MoneyTransactionModel;
import software.developer.bhushan.MoneyPe.db.models.UserAccountModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class UserTerminal {

    private static UserAccountModel userAccountModel;
    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private static String userID = "000000";

    private static String TYPE_DEPOSITE = "Deposite";
    private static String TYPE_WITHDRAW = "Withdraw";

    private boolean loggedIn = false;

    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public UserTerminal(UserAccountModel userAccountModel) {
        this.userAccountModel = userAccountModel;
    }

    public static void main(String[] args) throws Exception {
        userID = args[0];
        if (userID.isBlank()) {
            System.out.println("Error While Login Your Profile Please Retry.\n");
        } else {
            UserTerminal userTerminal = new UserTerminal(userAccountModel);
            userTerminal.Run();
        }
    }

    private void Run() throws Exception {
        userAccountModel = databaseHelper.getUserAccountTable().FetchByUserID(databaseHelper.getConnection(), userID);
        if (userAccountModel == null) {
            System.out.println("Error While Login Your Profile Please Retry.\n");
            loggedIn = false;
        } else {
            loggedIn = true;
            menu();
        }
    }

    public void Compile() throws Exception{
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "javac D:\\Bhushan\\Coding Projects\\Java\\Spring Boot Projects\\MoneyPe\\moneype\\src\\main\\java\\software\\developer\\bhushan\\moneype\\UserTerminal.java ");
        processBuilder.start();
    }

    public void Launch() throws Exception{
        try{
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/k", "start java software.developer.bhushan.moneype.UserTerminal " + userAccountModel.getUserId());
            processBuilder.start();
        }catch(Exception e){
            System.err.println(e);
            bufferedReader.readLine();
        }
    }

    // Method to display menu options and handle user input dynamically in a while
    // loop
    private void menu() throws Exception {
        while (loggedIn) {
            System.out.println("\n\tWelcome " + userAccountModel.getUserName() + "\n");
            System.out.println("1. View Account Details");
            System.out.println("2. Make a Transaction");
            System.out.println("3. Check Transaction History");
            System.out.println("4. Deposite Amount");
            System.out.println("5. Withdraw Amount");
            System.out.println("6. Logout");
            System.out.println("7. Delete Account.");
            System.out.print("\nChoose Your Option : ");

            // Capture user input
            String choice = bufferedReader.readLine();
            if (choice.equals("1")) {
                viewAccountDetails();
            } else if (choice.equals("2")) {
                makeTransaction();
            } else if (choice.equals("3")) {
                checkTransactionHistory();
            } else if (choice.equals("4")) {
                depositeWithraw(TYPE_DEPOSITE);
            } else if (choice.equals("5")) {
                depositeWithraw(TYPE_WITHDRAW);
            } else if (choice.equals("6")) {
                logout();
                loggedIn = false;
            } else if(choice.equals("7")){
                deleteUser();
            }
        }
    }

    // Example method to view account details
    private void viewAccountDetails() {
        userAccountModel = databaseHelper.getUserAccountTable().FetchByUserID(databaseHelper.getConnection(), userID);
        System.out.println("\nAccount details for " + userAccountModel.getUserName() + ":");
        // Logic to fetch and display user account details
        System.out.println("User ID : " + userAccountModel.getUserId());
        System.out.println("User Name : " + userAccountModel.getUserName());
        System.out.println("Date Of Accout Opening : " + userAccountModel.getUserDateOfCreation());
        System.out.println("Account Number : " + userAccountModel.getUserAccountNumber());
        System.out.println("Balance : " + userAccountModel.getUserBalance());
    }

    // Example method to make a transaction
    private void makeTransaction() throws Exception {
        ArrayList<UserAccountModel> userAccountModelArrayList = databaseHelper.getUserAccountTable()
                .FetchUserModelsInArrayList(databaseHelper.getConnection());
        if (userAccountModelArrayList.size() <= 1) {
            System.out.println("\n No Users Found For Transaction.");
        } else {
            System.out.println("\nYou Can Transfer Money To Following Users : ");
            System.out.println("\nName : Account Number");
            int i = 1;
            for (UserAccountModel uModel : userAccountModelArrayList) {
                if (uModel.getUserAccountNumber().equals(userAccountModel.getUserAccountNumber())) {
                    continue;
                }
                System.err.println(i + ". " + uModel.getUserName() + " : " + uModel.getUserAccountNumber());
                i++;
            }
            System.out.println(i + ". Exit");
            System.out.print("\nSelect User Index -> ");
            String ch = bufferedReader.readLine();
            if (ch.equals(i + "")) {
                System.out.println("\nTransaction Cancelled By User.");
                return;
            } else {
                UserAccountModel transferToUser;
                try {
                    int ich = Integer.parseInt(ch);
                    transferToUser = userAccountModelArrayList.get(i-1);
                } catch (Exception e) {
                    System.out.println("\nInvalid Option. Transaction Cancelled.");
                    return;
                }
                System.out.print("\nEnter Ammount To Transfer : ");
                try {
                    double amout = Double.parseDouble(bufferedReader.readLine());
                    if (amout > userAccountModel.getUserBalance()) {
                        System.out.println("\nInsufficient Balance. Transaction Cancelled.\n");
                        return;
                    }
                    String cDate = MoneyPe.getCurrentDate(), cTime = MoneyPe.getCurrentTime();
                    String cDateTime = cDate + " " + cTime;
                    MoneyTransactionModel moneyTransactionModel = new MoneyTransactionModel();
                    moneyTransactionModel.setMoneyAmount(amout);
                    moneyTransactionModel.setTransactionDate(cDate);
                    moneyTransactionModel.setTransactionTime(cTime);
                    moneyTransactionModel.setTransferFrom(userAccountModel.getUserAccountNumber());
                    moneyTransactionModel.setTransferTo(transferToUser.getUserAccountNumber());
                    moneyTransactionModel.setTransactionID(cDateTime);
                    boolean status = databaseHelper.getMoneyTransactionTable().Insert(databaseHelper.getConnection(),
                            moneyTransactionModel);
                    if (status) {
                        userAccountModel.setUserBalance(userAccountModel.getUserBalance()-amout);
                        transferToUser.setUserBalance(transferToUser.getUserBalance()+amout);
                        databaseHelper.getUserAccountTable().UpdateByUserID(databaseHelper.getConnection(),userAccountModel);
                        databaseHelper.getUserAccountTable().UpdateByUserID(databaseHelper.getConnection(),transferToUser);
                        System.out.println("\nTransaction Successful.\n");
                        System.out.println(moneyTransactionModel.getTransactionID());
                        System.out.println("Rs. " + amout + "/- is Successfuly Transfer To " + transferToUser.getUserName());
                        System.out.println("Available Balance : "+userAccountModel.getUserBalance());

                    } else {
                        System.out.println("\nSomething Error Occured During Transaction.\n");
                        System.out.println("Transaction Cancelled.\n");
                    }

                } catch (Exception e) {
                    System.out.println("\nInvalid Ammount. Transaction Cancelled.\n");
                }
            }

        }
    }

    private void checkTransactionHistory() {
        ArrayList<MoneyTransactionModel> moneyTransactionModelArrayList = databaseHelper.getMoneyTransactionTable()
                .FetchByTransferFromOrTransferTo(databaseHelper.getConnection(), userAccountModel.getUserAccountNumber());
        if (moneyTransactionModelArrayList.size() > 0) {
            System.out.println("\nTransactions : ");
            System.out.println("----------------------------------------------------");
            for (MoneyTransactionModel model : moneyTransactionModelArrayList) {
                System.out.println("\nTransaction Date : " + model.getTransactionDate());
                System.out.println("Transaction Time : " + model.getTransactionTime());

                if (model.getTransferTo().equals(TYPE_DEPOSITE) || model.getTransferTo().equals(TYPE_WITHDRAW)) {
                    System.out.println("Transaction Type : " + model.getTransferTo());
                } else {
                    UserAccountModel transferFrom = databaseHelper.getUserAccountTable().FetchByAccountNumber(
                            databaseHelper.getConnection(), model.getTransferFrom());
                    UserAccountModel transferTo = databaseHelper.getUserAccountTable().FetchByAccountNumber(
                            databaseHelper.getConnection(), model.getTransferTo());

                    System.out.println("Transfer From : " + transferFrom.getUserName() + " : "
                            + transferFrom.getUserAccountNumber());
                    System.out.println(
                            "Tranfer To : " + transferTo.getUserName() + " : " + transferTo.getUserAccountNumber());
                }

                System.out.println("Transaction Amount : " + model.getMoneyAmount());
                System.out.println("----------------------------------------------------");
            }
        } else {
            System.out.println("\nThere is No Transactions\n");
        }
    }

    private void depositeWithraw(String Type){
        
        System.out.print("\nEnter Ammount To "+Type+" : ");
        try{
            double amout = Double.parseDouble(bufferedReader.readLine());

            if(Type.equals(TYPE_WITHDRAW)){
                if (amout > userAccountModel.getUserBalance()){
                    System.out.println("\nInsufficient Balance. Transaction Cancelled.\n");
                    return;
                }
            }
    
            String cDate = MoneyPe.getCurrentDate(), cTime = MoneyPe.getCurrentTime();
            String cDateTime = cDate+" "+cTime;
            MoneyTransactionModel moneyTransactionModel = new MoneyTransactionModel();
            moneyTransactionModel.setMoneyAmount(amout);
            moneyTransactionModel.setTransactionDate(cDate);
            moneyTransactionModel.setTransactionTime(cTime);
            moneyTransactionModel.setTransferFrom(userAccountModel.getUserAccountNumber());
            moneyTransactionModel.setTransferTo(Type);
            moneyTransactionModel.setTransactionID(cDateTime);
            boolean status = databaseHelper.getMoneyTransactionTable().Insert(databaseHelper.getConnection(), moneyTransactionModel);
            if (status) {
                if (Type.equals(TYPE_DEPOSITE)) {
                    userAccountModel.setUserBalance(userAccountModel.getUserBalance()+amout);
                }else if (Type.equals(TYPE_WITHDRAW)) {
                    userAccountModel.setUserBalance(userAccountModel.getUserBalance()-amout);
                }
                databaseHelper.getUserAccountTable().UpdateByUserID(databaseHelper.getConnection(),userAccountModel);
                System.out.println("\nTransaction Successful.\n");
                System.out.println(moneyTransactionModel.getTransactionID());
                System.out.println("Rs. "+amout+"/- is Successfuly "+TYPE_DEPOSITE);
                System.out.println("Available Balance : "+userAccountModel.getUserBalance());
            }else{
                System.out.println("\nSomething Error Occured During Transaction.\n");
                System.out.println("Transaction Cancelled.\n");
            }
        }catch(Exception e){
            System.out.println("\nInvalid Ammount. Transaction Cancelled.\n");
        }
    }

    private void logout() {
        loggedIn = false;
        System.out.println("Logging out...");
    }

    private void deleteUser() throws Exception{
        databaseHelper.getMoneyTransactionTable().DeleteByTransferFromORTransferTo(databaseHelper.getConnection(), userAccountModel.getUserAccountNumber());
        databaseHelper.getUserAccountTable().DeleteUserByID(databaseHelper.getConnection(), userAccountModel);
        System.out.println("Your Account Deleted Successfully.");
        logout();
        System.out.println("Press Enter To Exit...");
        bufferedReader.readLine();

    }
}
