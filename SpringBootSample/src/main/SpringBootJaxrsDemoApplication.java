package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"main", "models", "resources", "services"})
public class SpringBootJaxrsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootJaxrsDemoApplication.class, args);
	}
}