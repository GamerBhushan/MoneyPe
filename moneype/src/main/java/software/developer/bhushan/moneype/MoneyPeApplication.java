package software.developer.bhushan.moneype;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication 
public class MoneyPeApplication {
	
	public static ArrayList<String> USERS_ARE_LOGINED = new ArrayList<>();


	public static void main(String[] args) throws Exception {
		AppProperties.AppName = "MoneyPe";
		SpringApplication.run(MoneyPeApplication.class, args);
	}

	public static boolean checkIsUserLogin(String accountNumber) {
		return USERS_ARE_LOGINED.stream().anyMatch(acNo -> acNo.equals(accountNumber));
	}

}
