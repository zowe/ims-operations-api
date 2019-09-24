package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.tran.create.CreateTransaction;
import application.rest.responses.tran.create.CreateTransactionOutput;
import application.rest.responses.tran.delete.DeleteTransaction;
import application.rest.responses.tran.delete.DeleteTransactionOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;



@ExtendWith({SuiteExtension.class})
public class TestBasicAuthenticationPostUser {

	private static final Logger logger = LoggerFactory.getLogger(TestBasicAuthenticationPostUser.class);
	private static final String ADMIN_USER = "admin";
	private static final String POST_USER = "post";
	private static final String DEFAULT_PASSWORD = "password";
	private static String PGM_PATH = TestProperties.contextPath + TestProperties.plex + "/program";
	private static String TRAN_PATH = TestProperties.contextPath + TestProperties.plex + "/transaction";
	private static String REGION_PATH = TestProperties.contextPath + TestProperties.plex + "/region";

	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
	}
	
	@Test
	public void testPostUserAccessPgmPostService() {
		logger.info("TESTING Post User Access Pgm POST Service");	
		// Will use ADMIN_USER for any necessary delete requests
		// Need to create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		Response response = RequestUtils.postRequest(queryParams, PGM_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	@Test
	public void testPostUserAccessTranPostService() {
		logger.info("TESTING Post User Access Tran POST Service");	
		// Will use ADMIN_USER for any necessary delete requests
		// Need to create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		// Create transaction
		logger.info("Create test transaction");
		List<String[]> queryParams1 = new ArrayList<>();
		String[] names1 = new String[] {"name", "TEST"};
		String[] pgm1 = new String[] {"pgm", "JUNIT"};
		queryParams1.add(names1);
		queryParams1.add(pgm1);
		Response response1 = RequestUtils.postRequest(queryParams1, TRAN_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response1.getStatus());
		CreateTransactionOutput ctr = RequestUtils.validateCTRSuccess(response1);
		/*Check if data is correct*/
		logger.info(ctr.toString());
		for (CreateTransaction q : ctr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getTran());
		}
		for (String key : ctr.getMessages().keySet()) {
			assertEquals(null, ctr.getMessages().get(key).getRc());
		}
		
		// Delete the transaction
		logger.info("Deleting test transaction");
		List<String[]> queryParams4 = new ArrayList<>();
		String[] names4 = new String[] {"name", "TEST"};
		queryParams4.add(names4);
		Response response4 = RequestUtils.deleteRequest(queryParams4, TRAN_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		DeleteTransactionOutput dtr4 = RequestUtils.validateDTRSuccess(response4);
		/*Check if data is correct*/
		logger.info(dtr4.toString());
		for (DeleteTransaction q : dtr4.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getTran());
		}
		for (String key : dtr4.getMessages().keySet()) {
			assertEquals(null, dtr4.getMessages().get(key).getRc());
		}
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	@Test
	public void testPostUserAccessPgmGetService() {
		logger.info("TESTING Post User Access Pgm GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPostUserAccessTranGetService() {
		logger.info("TESTING Post User Access Tran GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), TRAN_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPostUserAccessRegionGetService() {
		logger.info("TESTING Post User Access Region GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), REGION_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPostUserAccessPgmPutService() {
		logger.info("TESTING Post User Access Pgm PUT Service");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "DBF*"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		
		Response response = RequestUtils.putRequest(queryParams, PGM_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPostUserAccessTranPutService() {
		logger.info("TESTING Post User Access Tran PUT Service (stopping scheduling of a transaction)");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response response = RequestUtils.putRequest(queryParams, TRAN_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPostUserAccessRegionPutService() {
		logger.info("TESTING Post User Access Region PUT Service");
		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), REGION_PATH + "/stop", POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPostUserAccessPgmDeleteService() {
		logger.info("TESTING Post User Access Pgm DELETE Service");	
		// Will use ADMIN_USER for any necessary delete requests
		// Create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Attempt to delete test program JUNIT
		logger.info("Attempting to delete test program 'JUNIT'");
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "JUNIT"};
		queryParams2.add(names2);
		Response response = RequestUtils.deleteRequest(queryParams2, PGM_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	@Test
	public void testPostUserAccessTranDeleteService() {
		logger.info("TESTING Post User Access Tran DELETE Service");	
		// Will use ADMIN_USER for any necessary delete requests
		// Need to create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		// Create transaction
		logger.info("Create test transaction");
		List<String[]> queryParams1 = new ArrayList<>();
		String[] names1 = new String[] {"name", "TEST"};
		String[] pgm1 = new String[] {"pgm", "JUNIT"};
		queryParams1.add(names1);
		queryParams1.add(pgm1);
		Response response1 = RequestUtils.postRequest(queryParams1, TRAN_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		CreateTransactionOutput ctr = RequestUtils.validateCTRSuccess(response1);
		// Check if data is correct
		logger.info(ctr.toString());
		for (CreateTransaction q : ctr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getTran());
		}
		for (String key : ctr.getMessages().keySet()) {
			assertEquals(null, ctr.getMessages().get(key).getRc());
		}

		// Attempt to Delete the transaction -- Actual purpose of the function
		logger.info("Attempting to delete test transaction");
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "TEST"};
		queryParams2.add(names2);
		Response response2 = RequestUtils.deleteRequest(queryParams2, TRAN_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response2.getStatus());
		
		// Delete the transaction
		logger.info("Deleting test transaction");
		List<String[]> queryParams4 = new ArrayList<>();
		String[] names4 = new String[] {"name", "TEST"};
		queryParams4.add(names4);
		Response response4 = RequestUtils.deleteRequest(queryParams4, TRAN_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		DeleteTransactionOutput dtr4 = RequestUtils.validateDTRSuccess(response4);
		// Check if data is correct
		logger.info(dtr4.toString());
		for (DeleteTransaction q : dtr4.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getTran());
		}
		for (String key : dtr4.getMessages().keySet()) {
			assertEquals(null, dtr4.getMessages().get(key).getRc());
		}
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	
	
	
}
