package zowe.mc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.responses.QueryProgramResponses;

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
	public void testQueryPgm() throws Exception {
		logger.info("TESTING Query PGM");
		String path = "/pgm/";

		QueryProgramResponses responses =  webTarget.path(path).queryParam("names", "*").request(MediaType.APPLICATION_JSON).header("hostname", "ec32016a.vmec.svl.ibm.com")
				.header("port", "9999")
				.header("plex", "IM00P").accept(MediaType.APPLICATION_JSON).get(QueryProgramResponses.class);
		System.out.println("getting response");
		
		
		logger.info(responses.toString());
		assertNotEquals(null, responses);
		

	}

	
}
