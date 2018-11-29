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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icon.helpers.MCInteraction;
import om.connection.IconOmConnection;
import om.connection.IconOmConnectionFactory;
import om.exception.OmConnectionException;
import rs.responses.pgm.QueryProgramResponses;
import zowe.mc.TestProperties;

/**
 * Tests for IMS connections
 * @author jerryli
 *
 */
public class OMConnectionTest {

	private static MCInteraction mcSpec = new MCInteraction();
	private static final Logger logger = LoggerFactory.getLogger(OMConnectionTest.class);
	private static WebTarget webTarget = null;


	@BeforeAll
	public static void setUp() {
		
		mcSpec.setHostname(TestProperties.hostname);
		mcSpec.setPort(TestProperties.port);
		mcSpec.setImsPlexName(TestProperties.plex);
		
		//ResteasyClient client = new ResteasyClientBuilder().build();
		Client client = ClientBuilder.newClient();
		webTarget = client.target("http://localhost:9080/mc/services/");
	}

	/**
	 * Test connection to IMS
	 * @throws Exception
	 */
	@Test
	public void testImsConnection() throws Exception
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
	 * Negative test for bad connection
	 * @throws Exception
	 */
	@Test
	public void testBadConnection() {
		logger.info("TESTING Bad Connection");
		String path = "/pgm/";
		
		

		Invocation.Builder builder =  webTarget.path(path).queryParam("names", "*").request(MediaType.APPLICATION_JSON).header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.header("plex", "FOO").accept(MediaType.APPLICATION_JSON);
		
		Response responses = builder.get();
		
		QueryProgramResponses queryProgramResponses= responses.readEntity(QueryProgramResponses.class);
		
		//logger.info(queryProgramResponses.toString());
		assertNotEquals(null, queryProgramResponses);
		assertEquals(400, responses.getStatus());	

	}

}
