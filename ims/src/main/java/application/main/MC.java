/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"application.main", "application.services", "application.models", "application.resources", "application.rest", "filters"})
public class MC {

	public static void main(String[] args) {
		SpringApplication.run(MC.class, args);
	}
}