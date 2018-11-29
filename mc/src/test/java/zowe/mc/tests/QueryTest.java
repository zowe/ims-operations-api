package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

import rs.responses.pgm.QueryProgramResponses;

/**
 * Tests for QUERY IMS rest services
 * @author jerryli
 *
 */
public class QueryTest 
{

	private static final Logger logger = LoggerFactory.getLogger(QueryTest.class);
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
	public void testQueryPgm() {
		logger.info("TESTING Query PGM");
		String path = "/pgm/";
		
		

		Invocation.Builder builder =  webTarget.path(path).queryParam("names", "*").request(MediaType.APPLICATION_JSON).header("hostname", "ec32016a.vmec.svl.ibm.com")
				.header("port", "9999")
				.header("plex", "IM00P").accept(MediaType.APPLICATION_JSON);
		
		Response responses = builder.get();
		
		QueryProgramResponses queryProgramResponses= responses.readEntity(QueryProgramResponses.class);
		
		//logger.info(queryProgramResponses.toString());
		assertNotEquals(null, queryProgramResponses);
		assertEquals(200, responses.getStatus());	

	}
	
	/**
	 * Negative test for bad connection
	 * @throws Exception
	 */
	@Test
	public void testBadConnection() {
		logger.info("TESTING Bad Connection");
		String path = "/pgm/";
		
		

		Invocation.Builder builder =  webTarget.path(path).queryParam("names", "*").request(MediaType.APPLICATION_JSON).header("hostname", "ec32016a.vmec.svl.ibm.com")
				.header("port", "9999")
				.header("plex", "IM00").accept(MediaType.APPLICATION_JSON);
		
		Response responses = builder.get();
		
		QueryProgramResponses queryProgramResponses= responses.readEntity(QueryProgramResponses.class);
		
		//logger.info(queryProgramResponses.toString());
		assertNotEquals(null, queryProgramResponses);
		assertEquals(400, responses.getStatus());	

	}

	
}
