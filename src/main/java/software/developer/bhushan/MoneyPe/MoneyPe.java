package software.developer.bhushan.MoneyPe;

import software.developer.bhushan.MoneyPe.db.DatabaseHelper;
import software.developer.bhushan.MoneyPe.db.models.UserAccountModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;




public class MoneyPe {
    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private AccountNumberGenerator accountNumberGenerator;

    public MoneyPe() throws Exception{
        init();
        // Run();
    }

    public static void main(String[] args) throws Exception{
        MoneyPe moneyPe = new MoneyPe();
        moneyPe.Run();
    }

    private void init() throws Exception{
        accountNumberGenerator = new AccountNumberGenerator(databaseHelper);
        UserTerminal userTerminal = new UserTerminal(new UserAccountModel());
        userTerminal.Compile();
    }

    private void Run() throws Exception{
        String choice = "";
        do {
            menu();
            choice = bufferedReader.readLine();
            if (choice.equals("1")) {
                signIn();
            } else if (choice.equals("2")) {
                signUp();
            } else if (choice.equals("3")) {
                displayAllUsers();
            } else if (choice.equals("4")) {
                System.out.println("Exiting...");
            }
        } while (!choice.equals("4"));
    }

    private void menu(){
        System.out.println("\n\tWelcome To MoneyPe");
        System.out.println("\n1. Sign In. ( Login Existing User )");
        System.out.println("2. Sign Up. ( Create New User )");
        System.out.println("3. Display All Users.");
        System.out.println("4. Exit.");
        System.out.print("Choose Your Option -> ");
    }

    private void signIn() throws Exception{
        String userName, userPassword;
        System.out.print("\nEnter User Name : ");
        userName = bufferedReader.readLine();
        System.out.print("\nEnter Password : ");
        userPassword = bufferedReader.readLine();
        UserAccountModel userAccountModel = databaseHelper.getUserAccountTable().FetchUserAccountModelByUserNameAndPassword(databaseHelper.getConnection(),userName,userPassword);
        if (userAccountModel != null){
            System.out.println("\nLogin Successfully Starting Your Terminal.");
            UserTerminal userTerminal = new UserTerminal(userAccountModel);
            userTerminal.Launch();
        }else {
            System.out.println("\nInvalid Password or Username");
        }
    }


    private void signUp() throws Exception{
        UserAccountModel userAccountModel = new UserAccountModel();
        System.out.print("\nEnter User Name : ");
        userAccountModel.setUserName(bufferedReader.readLine());
        System.out.print("\nEnter Password : ");
        userAccountModel.setUserPassword(bufferedReader.readLine());

        String cDate = getCurrentDate(), cTime = getCurrentTime();
        String cDateTime = cDate+" "+cTime;
        String acNo = accountNumberGenerator.getUniqueAccountNumber();
        
        userAccountModel.setUserDateOfCreation(cDateTime);
        userAccountModel.setUserAccountNumber(acNo);
        userAccountModel.setUserBalance(0.0d);
        userAccountModel.setUserId(acNo);

        boolean status = databaseHelper.getUserAccountTable().Insert(databaseHelper.getConnection(), userAccountModel);
    }

    private void displayAllUsers(){
        ArrayList<UserAccountModel> userAccountModelArrayList =  databaseHelper.getUserAccountTable().FetchUserModelsInArrayList(databaseHelper.getConnection());
        if (userAccountModelArrayList.size()==0) {
            System.out.println("There Is No Users.");
        } else {
            System.out.println("-------------------------------------------------\n");
            for (UserAccountModel userModel : userAccountModelArrayList) {
                System.out.println("User Name : "+userModel.getUserName());
                System.out.println("Password : "+userModel.getUserPassword());
                System.out.println("AC No : "+userModel.getUserAccountNumber());
                System.out.println("Balance : Rs. "+userModel.getUserBalance()+"/-");
                System.out.println("Date Of Account Opening : "+userModel.getUserDateOfCreation());
                System.out.println("-------------------------------------------------\n");
            }
        }
    }


    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return formatter.format(date);
    }
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String getCurrentDateTime() {
        return getCurrentDate()+" "+getCurrentTime();
    }

    
}

