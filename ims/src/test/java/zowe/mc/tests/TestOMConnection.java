package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.pgm.query.QueryProgramOutput;
import icon.helpers.MCInteraction;
import om.connection.IconOmConnection;
import om.connection.IconOmConnectionFactory;
import om.exception.OmConnectionException;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for IMS connections
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestOMConnection {

	private static MCInteraction mcSpec = new MCInteraction();
	private static final Logger logger = LoggerFactory.getLogger(TestOMConnection.class);
	private static Client client;
	
	@BeforeAll
	public static void setUp() {

		mcSpec.setHostname(TestProperties.hostname);
		mcSpec.setPort(TestProperties.port);
		mcSpec.setImsPlexName(TestProperties.plex);
		client = ClientBuilder.newClient();
	}

	/**
	 * Test connection to IMS
	 */
	@Test
	public void testOmConnection()
	{
		logger.info("TESTING MC Connection");
		try {

			if (logger.isDebugEnabled()) logger.debug("Creating connection to " + mcSpec.getHostname() + ":" + mcSpec.getPort() + " - " + TestProperties.plex);

			IconOmConnectionFactory IconCF = new IconOmConnectionFactory();
			IconOmConnection omConnection = IconCF.createIconOmConnectionFromData(mcSpec);
			assertFalse(omConnection.isErrorInConnection());

			if (logger.isDebugEnabled()) logger.debug("Connection Successful!"); 
		} catch (OmConnectionException e) {
			logger.error("OmConnectionException", e);
			fail("Connection unsuccessful");
		}

	}


	/**
	 * Negative test for bad PLEX
	 */
	@Test
	public void testBadPlex() {
		
		logger.info("TESTING Bad Connection");
		String path = "FOO/program/";
		WebTarget webTarget = client.target("http://localhost:8081/");
		Invocation.Builder builder =  webTarget.path(path).queryParam("names", "*").request(MediaType.APPLICATION_JSON).header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.accept(MediaType.APPLICATION_JSON);

		Response responses = builder.get();

		QueryProgramOutput queryProgramResponses= responses.readEntity(QueryProgramOutput.class);
		//logger.info(queryProgramResponses.toString());
		assertNotEquals(null, queryProgramResponses);
		assertEquals(400, responses.getStatus());	

		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("4", queryProgramResponses.getMessages().get(key).getRc());
		}
	}
	
	/**
	 * Negative test for bad connection trigger
	 */
	@Test
	public void testBadConnection() {
		logger.info("TESTING Bad Connection");
		String path = "PLEX1/program/";
		WebTarget webTarget = client.target("http://localhost:8081/");
		Invocation.Builder builder =  webTarget.path(path).queryParam("names", "*").request(MediaType.APPLICATION_JSON).header("hostname", "FOO")
				.header("port", TestProperties.port)
				.accept(MediaType.APPLICATION_JSON);

		Response responses = builder.get();

		QueryProgramOutput queryProgramResponses= responses.readEntity(QueryProgramOutput.class);

		//logger.info(queryProgramResponses.toString());
		assertNotEquals(null, queryProgramResponses);
		assertEquals(400, responses.getStatus());	

		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("-1", queryProgramResponses.getMessages().get(key).getRc());
		}
	}
	
}
