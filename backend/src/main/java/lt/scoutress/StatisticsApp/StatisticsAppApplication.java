package lt.scoutress.StatisticsApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StatisticsAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatisticsAppApplication.class, args);
	}
}
