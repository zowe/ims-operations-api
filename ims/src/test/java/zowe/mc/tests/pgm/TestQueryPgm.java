package zowe.mc.tests.pgm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.pgm.query.QueryProgram;
import application.rest.responses.pgm.query.QueryProgramOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for "QUERY PGM" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestQueryPgm 
{

	private static final Logger logger = LoggerFactory.getLogger(TestQueryPgm.class);
	private static Client client;


	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
		client = ClientBuilder.newClient();
	}

	/**
	 * Tests rest service for submitting QUERY PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testQueryPgm() {
		logger.info("TESTING Query PGM");
		
		//QUERY PGM
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(),  "/" + TestProperties.plex + "/program", client);
		QueryProgramOutput qpr = RequestUtils.validateQPRSuccess(response);
		/*Check if data is correct*/
		logger.info(qpr.toString());
		for (QueryProgram q : qpr.getData()) {
			assertEquals("0", q.getCc());
		}
		for (String key : qpr.getMessages().keySet()) {
			assertEquals(null, qpr.getMessages().get(key).getRc());
		}
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
		String[] show = new String[] {"attributes", "TIMESTAMP"};
		queryParams.add(show);
		Response response = RequestUtils.getRequest(queryParams,  "/" + TestProperties.plex + "/program", client);
		QueryProgramOutput qpr = RequestUtils.validateQPRSuccess(response);
		/*Check if data is correct*/
		logger.info(qpr.toString());
		for (QueryProgram q : qpr.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getTmcr());
		}
		for (String key : qpr.getMessages().keySet()) {
			assertEquals(null, qpr.getMessages().get(key).getRc());
		}

		//SHOW=DOPT
		List<String[]> queryParams2 = new ArrayList<>();
		String[] show2 = new String[] {"attributes", "DOPT"};
		queryParams2.add(show2);
		Response response2= RequestUtils.getRequest(queryParams2,  "/" + TestProperties.plex + "/program", client);
		QueryProgramOutput qpr2 = RequestUtils.validateQPRSuccess(response2);
		/*Check if data is correct*/
		logger.info(qpr2.toString());
		for (QueryProgram q : qpr2.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getDopt());
		}
		for (String key : qpr2.getMessages().keySet()) {
			assertEquals(null, qpr2.getMessages().get(key).getRc());
		}

		//SHOW=SCHDTYPE
		List<String[]> queryParams3 = new ArrayList<>();
		String[] show3 = new String[] {"attributes", "SCHDTYPE"};
		queryParams3.add(show3);
		Response response3= RequestUtils.getRequest(queryParams3,  "/" + TestProperties.plex + "/program", client);
		QueryProgramOutput qpr3 = RequestUtils.validateQPRSuccess(response3);
		/*Check if data is correct*/
		logger.info(qpr3.toString());
		for (QueryProgram q : qpr3.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getSchd());
		}
		for (String key : qpr3.getMessages().keySet()) {
			assertEquals(null, qpr3.getMessages().get(key).getRc());
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
		Response response= RequestUtils.getRequest(queryParams,  "/" + TestProperties.plex + "/program", client);
		QueryProgramOutput qpr = response.readEntity(QueryProgramOutput.class);
		/*Check if data is correct*/
		logger.info(qpr.toString());
		for (QueryProgram q : qpr.getData()) {
			assertEquals("10", q.getCc());
		}
		for (String key : qpr.getMessages().keySet()) {
			assertEquals("0000000C", qpr.getMessages().get(key).getRc());
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
		Response response= RequestUtils.getRequest(queryParams, "/" + TestProperties.plex + "/program", client);
		QueryProgramOutput qpr = response.readEntity(QueryProgramOutput.class);
		/*Check if data is correct*/
		logger.info(qpr.toString());
		for (String key : qpr.getMessages().keySet()) {
			assertEquals("02000010", qpr.getMessages().get(key).getRc());
		}
		
	}
}
