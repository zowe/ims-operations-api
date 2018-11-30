package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

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

import rs.responses.pgm.StartProgramResponses;
import zowe.mc.TestProperties;

public class StartPgmTest {
	
	private static final Logger logger = LoggerFactory.getLogger(StartPgmTest.class);
	private static Client client;


	@BeforeAll
	public static void setUp() {

		client = ClientBuilder.newClient();
	}
	
	/**
	 * Tests rest service for submitting START PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testStartPgm() {
		logger.info("TESTING START PGM");
		
		StartProgramResponses startProgramResponses= request200(new ArrayList<String[]>());
		for (String key : startProgramResponses.getMessages().keySet()) {
			assertEquals("00000000", startProgramResponses.getMessages().get(key).getRc());
		}

	}
	
	/**
	 * Helper method for testing successful 200 rest requests. Specific to this classØ
	 * @param queryParams
	 * @return
	 */
	private StartProgramResponses request200(List<String[]> queryParams) {
		WebTarget webTarget = client.target("http://localhost:9080/mc/services/");
		String path = "/pgm/start";

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		Invocation.Builder builder =  webTarget.path(path).request(MediaType.APPLICATION_JSON).header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.header("plex", TestProperties.plex).accept(MediaType.APPLICATION_JSON);

		Response responses = builder.get();
		StartProgramResponses startProgramResponses = responses.readEntity(StartProgramResponses.class);

		/*Check if request is successful*/
		assertNotEquals(null, startProgramResponses);
		assertEquals(200, responses.getStatus());

		return startProgramResponses;
	}
	

}
