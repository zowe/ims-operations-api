package zowe.mc.tests.tran;

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

import application.rest.responses.tran.query.QueryTransaction;
import application.rest.responses.tran.query.QueryTransactionOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;

/**
 * Tests for "QUERY TRAN" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestQueryTran {
	
	private static final Logger logger = LoggerFactory.getLogger(TestQueryTran.class);
	private static Client client;


	@BeforeAll
	public static void setUp() {
		client = ClientBuilder.newClient();
	}
	
	/**
	 * Tests rest service for submitting "QUERY TRAN" IMS command
	 * @throws Exception
	 */
	@Test
	public void testQueryTran() {
		logger.info("TESTING Query TRAN");
		
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), "/tran/", client);
		QueryTransactionOutput qpr = RequestUtils.validateQTRSuccess(response);
		/*Check if data is correct*/
		logger.info(qpr.toString());
		for (QueryTransaction q : qpr.getData()) {
			assertEquals("0", q.getCc());
		}
		for (String key : qpr.getMessages().keySet()) {
			assertEquals("00000000", qpr.getMessages().get(key).getRc());
		}		
	}
	
	/**
	 * Tests rest service for submitting QUERY TRAN IMS command with different SHOW options
	 * @throws Exception
	 */
	@Test
	public void testQueryTranShow() {
		logger.info("TESTING Query TRAN with SHOW options");		
		
		//SHOW=TIMESTAMP
		List<String[]> queryParams = new ArrayList<>();
		String[] show = new String[] {"attributes", "TIMESTAMP"};
		queryParams.add(show);
		Response response = RequestUtils.getRequest(queryParams, "/tran/", client);
		QueryTransactionOutput qpr = RequestUtils.validateQTRSuccess(response);
		/*Check if data is correct*/
		logger.info(qpr.toString());
		for (QueryTransaction q : qpr.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getTmcr());
		}
		for (String key : qpr.getMessages().keySet()) {
			assertEquals("00000000", qpr.getMessages().get(key).getRc());
		}

		//SHOW=DOPT
		List<String[]> queryParams2 = new ArrayList<>();
		String[] show2 = new String[] {"attributes", "PGM"};
		queryParams2.add(show2);
		Response response2= RequestUtils.getRequest(queryParams2, "/tran/", client);
		QueryTransactionOutput qpr2 = RequestUtils.validateQTRSuccess(response2);
		/*Check if data is correct*/
		logger.info(qpr2.toString());
		for (QueryTransaction q : qpr2.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getPsb());
		}
		for (String key : qpr2.getMessages().keySet()) {
			assertEquals("00000000", qpr2.getMessages().get(key).getRc());
		}

		//SHOW=SCHDTYPE
		List<String[]> queryParams3 = new ArrayList<>();
		String[] show3 = new String[] {"attributes", "AOCMD"};
		queryParams3.add(show3);
		Response response3= RequestUtils.getRequest(queryParams3, "/tran/", client);
		QueryTransactionOutput qpr3 = RequestUtils.validateQTRSuccess(response3);
		/*Check if data is correct*/
		logger.info(qpr3.toString());
		for (QueryTransaction q : qpr3.getData()) {
			assertEquals("0", q.getCc());
			assertNotNull(q.getAocmd());
		}
		for (String key : qpr3.getMessages().keySet()) {
			assertEquals("00000000", qpr3.getMessages().get(key).getRc());
		}

	}
	
	/**
	 * Tests rest service for submitting QUERY PGM IMS command with incorrect program name
	 * @throws Exception
	 */
	@Test
	public void testBadTranName() {
		logger.info("TESTING Query PGM with bad pgm name");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] show = new String[] {"names", "FOO"};
		queryParams.add(show);
		Response response= RequestUtils.getRequest(queryParams, "/tran/", client);
		QueryTransactionOutput qpr = response.readEntity(QueryTransactionOutput.class);
		/*Check if data is correct*/
		logger.info(qpr.toString());
		for (QueryTransaction q : qpr.getData()) {
			assertEquals("10", q.getCc());
		}
		for (String key : qpr.getMessages().keySet()) {
			assertEquals("0000000C", qpr.getMessages().get(key).getRc());
		}
	}
	
	
	

}
