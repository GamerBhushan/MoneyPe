package software.developer.bhushan.MoneyPe;

import software.developer.bhushan.MoneyPe.db.DatabaseHelper;
import software.developer.bhushan.MoneyPe.db.models.UserAccountModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;



public class AccountNumberGenerator {

    private static final Set<Long> generatedNumbers = new HashSet<>();
    private static final Random random = new Random();
    
    private DatabaseHelper databaseHelper;

    public AccountNumberGenerator(DatabaseHelper databaseHelper){
        this.databaseHelper = databaseHelper;
    }

    private static long generateUnique10DigitNumber() {
        long randomNumber;
        do {
            // Generate a 10-digit number by setting bounds from 1,000,000,000 to 9,999,999,999
            randomNumber = 1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L);
        } while (generatedNumbers.contains(randomNumber)); // Ensure it's unique

        generatedNumbers.add(randomNumber); // Store to prevent duplicates
        return randomNumber;
    }

    public String getUniqueAccountNumber(){
        ArrayList<UserAccountModel> userAccountModelArrayList;
        long acLong;
        String acStr;
        do {
            acLong = generateUnique10DigitNumber();
            acStr = Long.toString(acLong);
            userAccountModelArrayList = databaseHelper.getUserAccountTable().FetchUserModelsInArrayList(databaseHelper.getConnection());
        } while(checkIfAlreadyExists(userAccountModelArrayList, acStr));

        return acStr;
    }
    
    private boolean checkIfAlreadyExists(ArrayList<UserAccountModel> userAccountModelArrayList, String acStr){
        for(UserAccountModel uModel : userAccountModelArrayList){
            if(uModel.getUserAccountNumber().equals(acStr)){
                return true;
            }
        }
        return false;
    }

}
