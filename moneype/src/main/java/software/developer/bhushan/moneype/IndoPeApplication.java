package software.developer.bhushan.moneype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IndoPeApplication {
     
    public static void main(String[] args) throws Exception {
        AppProperties.AppName = "IndoPe";
		SpringApplication.run(MoneyPeApplication.class, args);
	}
}
