package de.uni_leipzig.digital_text_forensics;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan({"de.uni_leipzig.digital_text_forensics"})
@EnableEmailTools
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
