package zowe.mc.tests;

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

import application.rest.responses.pgm.QueryProgramResponse;
import application.rest.responses.pgm.QueryProgramResponses;
import application.rest.responses.pgm.UpdatePgmResponses;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for "UPDATE PGM" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestUpdatePgm {
	
	private static final Logger logger = LoggerFactory.getLogger(TestUpdatePgm.class);
	private static Client client;
	
	/**
	 * Setup rest client
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
	public void testUpdatePgmStopSchd() {

		//First we stop the program
		logger.info("Testing Update PGM by stopping scheduling of a program");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"names", "DBF*"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response responses = RequestUtils.putRequest(queryParams, "/pgm/", client);
		UpdatePgmResponses upr = RequestUtils.validateUPRSuccess(responses);
		logger.info(upr.toString());
		
		//Then we verify it's status
		logger.info("Verifying status");	
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"names", "DBF*"};
		String[] show = new String[] {"attributes", "ALL"};
		queryParams2.add(names2);
		queryParams2.add(show);
		Response responses2 = RequestUtils.getRequest(queryParams2, "/pgm/", client);
		QueryProgramResponses qpr = RequestUtils.validateQPRSuccess(responses2);
		for (QueryProgramResponse r : qpr.getData()) {
			assertEquals("STOSCHD", r.getLstt());
		}
		
		//First we start the program
		logger.info("Testing Update PGM by stopping scheduling of a program");		
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"names", "DBF*"};
		String[] start = new String[] {"start", "SCHD"};
		queryParams3.add(names3);
		queryParams3.add(start);
		Response responses3 = RequestUtils.putRequest(queryParams3, "/pgm/", client);
		UpdatePgmResponses upr2 = RequestUtils.validateUPRSuccess(responses3);
		logger.info(upr2.toString());
		
		//Then we verify it's status
		logger.info("Verifying status");	
		List<String[]> queryParams4 = new ArrayList<>();
		String[] names4 = new String[] {"names", "DBF*"};
		String[] show2 = new String[] {"attributes", "ALL"};
		queryParams4.add(names4);
		queryParams4.add(show2);
		Response responses4 = RequestUtils.getRequest(queryParams4, "/pgm/", client);
		QueryProgramResponses qpr2 = RequestUtils.validateQPRSuccess(responses4);
		for (QueryProgramResponse r : qpr2.getData()) {
			assertEquals(null, r.getLstt());
		}
		
		
	}
	
	

}
