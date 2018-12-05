package main;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * @author Amit Kumar
 */
@Component
public class JerseyConfig extends ResourceConfig{
	public JerseyConfig() {
		packages("resources");
	}
}