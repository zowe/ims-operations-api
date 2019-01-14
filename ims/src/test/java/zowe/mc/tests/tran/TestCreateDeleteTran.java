package zowe.mc.tests.tran;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import application.rest.responses.pgm.create.CreateProgram;
import application.rest.responses.pgm.create.CreateProgramOutput;
import application.rest.responses.pgm.delete.DeleteProgram;
import application.rest.responses.pgm.delete.DeleteProgramOutput;
import application.rest.responses.tran.create.CreateTransaction;
import application.rest.responses.tran.create.CreateTransactionOutput;
import application.rest.responses.tran.delete.DeleteTransaction;
import application.rest.responses.tran.delete.DeleteTransactionOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;

	/**
	 * Tests for "CREATE/DELETE TRAN" IMS rest services
	 * @author jerryli
	 *
	 */
	@ExtendWith({SuiteExtension.class})
	public class TestCreateDeleteTran 
	{

		private static final Logger logger = LoggerFactory.getLogger(TestCreateDeleteTran.class);
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
		public void testCreateDeleteTran() {
			logger.info("TESTING CREATE and DELETE TRAN");
			
			logger.info("Need to create program first");
			List<String[]> queryParams = new ArrayList<>();
			String[] names = new String[] {"names", "TEST"};
			queryParams.add(names);
			Response response = RequestUtils.postRequest(queryParams, "/PLEX1/program", client);
			CreateProgramOutput cpr = RequestUtils.validateCPRSuccess(response);
			/*Check if data is correct*/
			logger.info(cpr.toString());
			for (CreateProgram q : cpr.getData()) {
				assertEquals("0", q.getCc());
				assertEquals("TEST", q.getPgm());
			}
			for (String key : cpr.getMessages().keySet()) {
				assertEquals("00000000", cpr.getMessages().get(key).getRc());
			}
			
			
			List<String[]> queryParams1 = new ArrayList<>();
			String[] names1 = new String[] {"names", "TEST"};
			String[] pgm1 = new String[] {"pgm", "TEST"};
			queryParams1.add(names1);
			queryParams1.add(pgm1);
			Response response1 = RequestUtils.postRequest(queryParams1, "/PLEX1/transaction", client);
			CreateTransactionOutput ctr = RequestUtils.validateCTRSuccess(response1);
			/*Check if data is correct*/
			logger.info(ctr.toString());
			for (CreateTransaction q : ctr.getData()) {
				assertEquals("0", q.getCc());
				assertEquals("TEST", q.getTran());
			}
			for (String key : ctr.getMessages().keySet()) {
				assertEquals("00000000", ctr.getMessages().get(key).getRc());
			}
			
			List<String[]> queryParams2 = new ArrayList<>();
			String[] names2 = new String[] {"names", "TEST"};
			queryParams2.add(names2);
			Response response2 = RequestUtils.deleteRequest(queryParams2, "/PLEX1/transaction", client);
			DeleteTransactionOutput dtr = RequestUtils.validateDTRSuccess(response2);
			/*Check if data is correct*/
			logger.info(dtr.toString());
			for (DeleteTransaction q : dtr.getData()) {
				assertEquals("0", q.getCc());
				assertEquals("TEST", q.getTran());
			}
			for (String key : dtr.getMessages().keySet()) {
				assertEquals("00000000", dtr.getMessages().get(key).getRc());
			}
			
			List<String[]> queryParams3 = new ArrayList<>();
			String[] names3 = new String[] {"names", "TEST"};
			queryParams3.add(names3);
			Response response3= RequestUtils.deleteRequest(queryParams3, "/PLEX1/program", client);
			DeleteProgramOutput dpr = RequestUtils.validateDPRSuccess(response3);
			/*Check if data is correct*/
			logger.info(dpr.toString());
			for (DeleteProgram q : dpr.getData()) {
				assertEquals("0", q.getCc());
				assertEquals("TEST", q.getPgm());
			}
			for (String key : dpr.getMessages().keySet()) {
				assertEquals("00000000", dpr.getMessages().get(key).getRc());
			}
			
		
			


		}
	}