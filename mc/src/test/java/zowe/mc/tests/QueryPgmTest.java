package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import application.rest.responses.pgm.QueryProgramResponse;
import application.rest.responses.pgm.QueryProgramResponses;
import zowe.mc.TestProperties;

/**
 * Tests for QUERY IMS rest services
 * @author jerryli
 *
 */
public class QueryPgmTest 
{

	private static final Logger logger = LoggerFactory.getLogger(QueryPgmTest.class);
	private static Client client;


	/**
	 * Setup rest client and webtarget address
	 */
	@BeforeAll
	public static void setUp() {

		client = ClientBuilder.newClient();
	}

	/**
	 * Tests rest service for submitting QUERY PGM IMS command with different SHOW options
	 * @throws Exception
	 */
	@Test
	public void testQueryPgmShow() {
		logger.info("TESTING Query PGM with SHOW options");		
		
		//SHOW=TIMESTAMP
		List<String[]> queryParams = new ArrayList<>();
		String[] show = new String[] {"show", "TIMESTAMP"};
		queryParams.add(show);
		QueryProgramResponses queryProgramResponses= request200(queryParams);
		/*Check if data is correct*/
		logger.info(queryProgramResponses.toString());
		for (QueryProgramResponse q : queryProgramResponses.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getTmcr());
		}
		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("00000000", queryProgramResponses.getMessages().get(key).getRc());
		}

		//SHOW=DOPT
		List<String[]> queryParams2 = new ArrayList<>();
		String[] show2 = new String[] {"show", "DOPT"};
		queryParams2.add(show2);
		QueryProgramResponses queryProgramResponses2= request200(queryParams2);
		/*Check if data is correct*/
		logger.info(queryProgramResponses2.toString());
		for (QueryProgramResponse q : queryProgramResponses2.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getDopt());
		}
		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("00000000", queryProgramResponses.getMessages().get(key).getRc());
		}

		//SHOW=SCHDTYPE
		List<String[]> queryParams3 = new ArrayList<>();
		String[] show3 = new String[] {"show", "SCHDTYPE"};
		queryParams3.add(show3);
		QueryProgramResponses queryProgramResponses3= request200(queryParams3);
		/*Check if data is correct*/
		logger.info(queryProgramResponses3.toString());
		for (QueryProgramResponse q : queryProgramResponses3.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getSchd());
		}
		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("00000000", queryProgramResponses.getMessages().get(key).getRc());
		}

	}


	/**
	 * Tests rest service for submitting QUERY PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testQueryPgm() {
		logger.info("TESTING Query PGM");
		
		//QUERY PGM
		QueryProgramResponses queryProgramResponses = request200(new ArrayList<String[]>());
		/*Check if data is correct*/
		logger.info(queryProgramResponses.toString());
		for (QueryProgramResponse q : queryProgramResponses.getData()) {
			assertEquals("0", q.getCc());
		}
		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("00000000", queryProgramResponses.getMessages().get(key).getRc());
		}
	}

	/**
	 * Tests rest service for submitting QUERY PGM IMS command with incorrect program name
	 * @throws Exception
	 */
	@Test
	public void testBadPgmName() {
		logger.info("TESTING Query PGM with bad pgm name");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] show = new String[] {"names", "FOO"};
		queryParams.add(show);
		QueryProgramResponses queryProgramResponses= request200(queryParams);
		/*Check if data is correct*/
		logger.info(queryProgramResponses.toString());
		for (QueryProgramResponse q : queryProgramResponses.getData()) {
			assertEquals("10", q.getCc());
		}
		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("0000000C", queryProgramResponses.getMessages().get(key).getRc());
		}
	}
	
	/**
	 * Negative test for bad route
	 * @
	 */
	@Test
	public void testBadRoute() {
		logger.info("TESTING Bad Route");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] route = new String[] {"route", "FOO"};
		queryParams.add(route);
		QueryProgramResponses queryProgramResponses = request200(queryParams);
		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("02000010", queryProgramResponses.getMessages().get(key).getRc());
		}
		
	}

	/**
	 * Helper method for testing successful 200 rest requests. Specific to this class
	 * @param queryParams
	 * @return
	 */
	private QueryProgramResponses request200(List<String[]> queryParams) {
		WebTarget webTarget = client.target("http://localhost:9080/mc/services/");
		String path = "/pgm/";

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		Invocation.Builder builder =  webTarget.path(path).request(MediaType.APPLICATION_JSON).header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.header("plex", TestProperties.plex).accept(MediaType.APPLICATION_JSON);

		Response responses = builder.get();
		QueryProgramResponses queryProgramResponses = responses.readEntity(QueryProgramResponses.class);

		/*Check if request is successful*/
		assertNotEquals(null, queryProgramResponses);
		assertEquals(200, responses.getStatus());

		return queryProgramResponses;
	}

}
