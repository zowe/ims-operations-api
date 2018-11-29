package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.responses.pgm.StartProgramResponses;
import zowe.mc.TestProperties;

public class StartTest {
	
	private static final Logger logger = LoggerFactory.getLogger(StartTest.class);
	private static WebTarget webTarget = null;

	
	/**
	 * Setup rest client and webtarget address
	 */
	@BeforeAll
	public static void setUp() {
		
		//ResteasyClient client = new ResteasyClientBuilder().build();
		Client client = ClientBuilder.newClient();
		webTarget = client.target("http://localhost:9080/mc/services/");

	}
	
	/**
	 * Tests rest service for submitting QUERY PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testStartPgm() {
		logger.info("TESTING START PGM");
		String path = "/pgm/start";
		
		Invocation.Builder builder =  webTarget.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true).path(path).request(MediaType.APPLICATION_JSON).header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.header("plex", TestProperties.plex).accept(MediaType.APPLICATION_JSON);
		
		Response responses = builder.put(Entity.json(null));
		
		StartProgramResponses startProgramResponses= responses.readEntity(StartProgramResponses.class);
		
		//logger.info(queryProgramResponses.toString());
		assertNotEquals(null, startProgramResponses);
		assertEquals(200, responses.getStatus());	

	}
	

}
