package software.developer.bhushan.MoneyPe.controllers;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.lang.Nullable;
import software.developer.bhushan.MoneyPe.*;
import software.developer.bhushan.MoneyPe.db.DatabaseHelper;
import software.developer.bhushan.MoneyPe.db.models.MoneyTransactionModel;
import software.developer.bhushan.MoneyPe.db.models.UserAccountModel;
;

@Controller
public class MoneyPeController {

    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private AccountNumberGenerator accountNumberGenerator = new AccountNumberGenerator(databaseHelper);

    @RequestMapping("/")
    public String handle(Model model) {
        model.addAttribute("AppName", AppProperties.AppName);
        return "index";
    }

    @RequestMapping("/signin")
    public String SignIn(Model model) {
        model.addAttribute("AppName", AppProperties.AppName);
        return "signin";
    }

    @RequestMapping("/signup")
    public String SignUp(Model model) {
        model.addAttribute("AppName", AppProperties.AppName);
        return "signup";
    }

    @PostMapping("/signup")
    public String precessSignUp(@RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "password", required = false) String password, Model model) {
                
        model.addAttribute("AppName", AppProperties.AppName);

        if ((username != null && password != null) && (!username.isBlank())) {

            try {
                UserAccountModel userAccountModel = databaseHelper.getUserAccountTable()
                        .FetchUserAccountModelByUserNameAndPassword(databaseHelper.getConnection(), username, password);
                if (userAccountModel != null) {
                    return "redirect:/home/" + userAccountModel.getUserAccountNumber();
                }
                userAccountModel = new UserAccountModel();
                String acno = accountNumberGenerator.getUniqueAccountNumber();
                userAccountModel.setUserId(acno);
                userAccountModel.setUserAccountNumber(acno);
                userAccountModel.setUserName(username);
                userAccountModel.setUserPassword(password);
                userAccountModel.setUserDateOfCreation(MoneyPe.getCurrentDateTime());
                userAccountModel.setUserBalance(0.00);
                databaseHelper.getUserAccountTable().Insert(databaseHelper.getConnection(), userAccountModel);
                return "redirect:/home/" + acno;
            } catch (Exception e) {
                model.addAttribute("alertMessage", e.toString());
                model.addAttribute("alertType", "danger");
            }
        } else {
            model.addAttribute("alertMessage", "Please Enter Valid Username Or Password");
            model.addAttribute("alertType", "danger");
        }
        return "signup";
    }

    public String Home() {
        return "home";
    }

    @RequestMapping({ "/home/", "/home/{acNo}" })
    public String HomeWithSignin(@Nullable @PathVariable(name = "acNo", required = false) String acNo, Model model) {
        if (acNo == null) {
            return "signin";
        }
        if (MoneyPeApplication.checkIsUserLogin(acNo)) {
            model.addAttribute("acNo", acNo);
            UserAccountModel userAccountModel = databaseHelper.getUserAccountTable()
                    .FetchByAccountNumber(databaseHelper.getConnection(), acNo);
            ArrayList<MoneyTransactionModel> transactions = databaseHelper.getMoneyTransactionTable()
                    .FetchByTransferFromOrTransferTo(databaseHelper.getConnection(), acNo);
            model.addAttribute("userAccountModel", userAccountModel);

            model.addAttribute("transactions", transactions);
            return "home";
        } else {
            return "signin";
        }
    }

    @PostMapping("/signin")
    public String processSignIn(@RequestParam("username") String username, @RequestParam("password") String password,
            Model model) {

                String acNo = authenticateUser(username, password);

        if (acNo.length() > 0) {
            model.addAttribute("acNo", acNo);
            return "redirect:/home/" + acNo;
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "signin"; // Reloads the sign-in page with an error message
        }
    }

    private String authenticateUser(String username, String password) {
        UserAccountModel user = databaseHelper.getUserAccountTable()
                .FetchUserAccountModelByUserNameAndPassword(databaseHelper.getConnection(), username, password);
        if (user == null) {
            return "";
        }
        MoneyPeApplication.USERS_ARE_LOGINED.add(user.getUserAccountNumber());
        return user.getUserAccountNumber();
    }

    @RequestMapping("/display-all-users")
    public String displayAllUsers(Model model) {
        ArrayList<UserAccountModel> allUsers = databaseHelper.getUserAccountTable()
                .FetchUserModelsInArrayList(databaseHelper.getConnection());
        model.addAttribute("AppName", AppProperties.AppName);
        model.addAttribute("allUsers", allUsers);
        return "display-all-users";
    }

    // Handling Menus

    @PostMapping("/logout")
    public String logout(@RequestParam(name = "userId", required = false) String userId) {
        if (userId != null && !userId.isEmpty()) {
            try {
                boolean removed = MoneyPeApplication.USERS_ARE_LOGINED.remove(userId);
                if (removed) {
                    System.out.println("Successfully logged out: " + userId);
                } else {
                    System.err.println("User ID not found in the login list: " + userId);
                }
            } catch (Exception e) {
                System.err.println("Error occurred while logging out user: " + e.getMessage());
            }
        } else {
            System.err.println("Invalid or missing user ID for logout.");
        }
        return "redirect:/";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(@RequestParam(name = "userId", required = false) String userId) {
        if (userId != null && !userId.isEmpty()) {
            try {
                boolean removed = MoneyPeApplication.USERS_ARE_LOGINED.remove(userId);
                databaseHelper.getUserAccountTable().DeleteUserByID(databaseHelper.getConnection(), userId);
                if (removed) {
                    System.out.println("Account Deleted Successfully : " + userId);
                } else {
                    System.err.println("Unable To Delete : User ID not found in the login list : " + userId);
                }
            } catch (Exception e) {
                System.err.println("Error occurred while delete user: " + e.getMessage());
            }
        } else {
            System.err.println("Invalid or missing user ID for delete.");
        }
        return "redirect:/";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam(name = "userId") String userId, @RequestParam(name = "amount") double amount,
            RedirectAttributes redirectAttributes) {

        UserAccountModel userAccountModel = databaseHelper.getUserAccountTable()
                .FetchByUserID(databaseHelper.getConnection(), userId);

        if (userAccountModel != null && userAccountModel.getUserBalance() >= amount
                && MoneyPeApplication.checkIsUserLogin(userId)) {

            // Update user balance
            userAccountModel.setUserBalance(userAccountModel.getUserBalance() - amount);
            databaseHelper.getUserAccountTable().UpdateByUserID(databaseHelper.getConnection(), userAccountModel);

            // Record the transaction
            MoneyTransactionModel moneyTransactionModel = new MoneyTransactionModel();
            String cT = MoneyPe.getCurrentTime(), cD = MoneyPe.getCurrentDate(), cTD = cT + " " + cD;

            moneyTransactionModel.setTransactionID(cTD);
            moneyTransactionModel.setTransactionDate(cD);
            moneyTransactionModel.setTransactionTime(cT);
            moneyTransactionModel.setTransferFrom(userId);
            moneyTransactionModel.setTransferTo(TransactionTypes.TT_TYPE_WITHDRAW);
            moneyTransactionModel.setMoneyAmount(amount);

            databaseHelper.getMoneyTransactionTable().Insert(databaseHelper.getConnection(), moneyTransactionModel);

            // Set success message
            redirectAttributes.addFlashAttribute("alertMessage", "$ " + amount + " Withdraw Successfully.");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } else {
            // Set error message
            redirectAttributes.addFlashAttribute("alertMessage", "Insufficient balance or user not found.");
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }

        return "redirect:/home/" + userId; // Redirect to the user's home page
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam(name = "userId") String userId, @RequestParam(name = "amount") double amount,
            RedirectAttributes redirectAttributes) {
        UserAccountModel userAccountModel = databaseHelper.getUserAccountTable()
                .FetchByUserID(databaseHelper.getConnection(), userId);

        if (userAccountModel != null && amount > 0 && MoneyPeApplication.checkIsUserLogin(userId)) {
            userAccountModel.setUserBalance(userAccountModel.getUserBalance() + amount);
            databaseHelper.getUserAccountTable().UpdateByUserID(databaseHelper.getConnection(), userAccountModel);

            MoneyTransactionModel moneyTransactionModel = new MoneyTransactionModel();
            String cT = MoneyPe.getCurrentTime(), cD = MoneyPe.getCurrentDate(), cTD = cT + " " + cD;

            moneyTransactionModel.setTransactionID(cTD);
            moneyTransactionModel.setTransactionDate(cD);
            moneyTransactionModel.setTransactionTime(cT);
            moneyTransactionModel.setTransferFrom(userId);
            moneyTransactionModel.setTransferTo(TransactionTypes.TT_TYPE_DEPOSITE);
            moneyTransactionModel.setMoneyAmount(amount);

            databaseHelper.getMoneyTransactionTable().Insert(databaseHelper.getConnection(), moneyTransactionModel);

            redirectAttributes.addFlashAttribute("alertMessage", "$ " + amount + " Deposited. ");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } else {
            redirectAttributes.addFlashAttribute("alertMessage", "Something went wrong. Please try again.");
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }

        return "redirect:/home/" + userId; // Redirect to the user's home page
    }

    @PostMapping("/makeTransaction")
    public String makeTransaction(@RequestParam(name = "transferFromID") String transferFromID,
            @RequestParam(name = "transferToID") String transferToID, @RequestParam(name = "amount") double amount,
            RedirectAttributes redirectAttributes) {

        if (transferToID == null || transferToID.isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Invalid recipient User ID.");
            redirectAttributes.addFlashAttribute("alertType", "danger");
            return "redirect:/home/" + transferFromID;
        }

        if (amount <= 0) {
            redirectAttributes.addFlashAttribute("alertMessage",
                    "Invalid transaction amount. Must be greater than ₹0.");
            redirectAttributes.addFlashAttribute("alertType", "danger");
            return "redirect:/home/" + transferFromID;
        }

        UserAccountModel transferFrom = databaseHelper.getUserAccountTable()
                .FetchByUserID(databaseHelper.getConnection(), transferFromID);
        UserAccountModel transferTo = databaseHelper.getUserAccountTable().FetchByUserID(databaseHelper.getConnection(),
                transferToID);

        if (transferFrom == null || transferTo == null || !MoneyPeApplication.checkIsUserLogin(transferFromID)) {
            redirectAttributes.addFlashAttribute("alertMessage", "Invalid user accounts or unauthorized transaction.");
            redirectAttributes.addFlashAttribute("alertType", "danger");
            return "redirect:/home/" + transferFromID;
        }

        if (transferFrom.getUserBalance() < amount) {
            redirectAttributes.addFlashAttribute("alertMessage", "Insufficient balance for the transaction.");
            redirectAttributes.addFlashAttribute("alertType", "danger");
            return "redirect:/home/" + transferFromID;
        }

        // Perform Transaction
        transferFrom.setUserBalance(transferFrom.getUserBalance() - amount);
        transferTo.setUserBalance(transferTo.getUserBalance() + amount);

        databaseHelper.getUserAccountTable().UpdateByUserID(databaseHelper.getConnection(), transferFrom);
        databaseHelper.getUserAccountTable().UpdateByUserID(databaseHelper.getConnection(), transferTo);

        // Record Transaction
        MoneyTransactionModel moneyTransactionModel = new MoneyTransactionModel();
        String cT = MoneyPe.getCurrentTime(), cD = MoneyPe.getCurrentDate(), cTD = cT + " " + cD;

        moneyTransactionModel.setTransactionID(cTD);
        moneyTransactionModel.setTransactionDate(cD);
        moneyTransactionModel.setTransactionTime(cT);
        moneyTransactionModel.setTransferFrom(transferFromID);
        moneyTransactionModel.setTransferTo(transferToID);
        moneyTransactionModel.setMoneyAmount(amount);

        databaseHelper.getMoneyTransactionTable().Insert(databaseHelper.getConnection(), moneyTransactionModel);

        redirectAttributes.addFlashAttribute("alertMessage",
                "Transaction Successful! Transferred $" + amount + " To " + transferTo.getUserName());
        redirectAttributes.addFlashAttribute("alertType", "success");

        return "redirect:/home/" + transferFromID;
    }

}
