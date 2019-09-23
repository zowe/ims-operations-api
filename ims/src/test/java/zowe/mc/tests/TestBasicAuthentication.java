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

import application.rest.responses.pgm.create.CreateProgram;
import application.rest.responses.pgm.create.CreateProgramOutput;
import application.rest.responses.pgm.delete.DeleteProgram;
import application.rest.responses.pgm.delete.DeleteProgramOutput;
import application.rest.responses.tran.create.CreateTransaction;
import application.rest.responses.tran.create.CreateTransactionOutput;
import application.rest.responses.tran.delete.DeleteTransaction;
import application.rest.responses.tran.delete.DeleteTransactionOutput;
import application.rest.responses.tran.update.UpdateTransactionOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

@ExtendWith({SuiteExtension.class})
public class TestBasicAuthentication {
	
	private static final Logger logger = LoggerFactory.getLogger(TestBasicAuthentication.class);
	private static final String ADMIN_USER = "admin";
	private static final String GET_USER = "get";
	private static final String POST_USER = "post";
	private static final String PUT_USER = "put";
	private static final String PGM_USER = "pgm";
	private static final String REGION_USER = "region";
	private static final String DELETE_USER = "delete";
	private static final String TRAN_USER = "tran";
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
	public void testNoCredentials() {
		logger.info("TESTING No Credentials");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, null, null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testCorrectAdminCredentials() {
		logger.info("TESTING Correct Admin Credentials");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void testPgmUserAccessPgmGetService() {
		logger.info("TESTING Pgm User Access Pgm GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessPgmPutService() {
		logger.info("TESTING Pgm User Access Pgm PUT Service");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "DBF*"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		
		Response response = RequestUtils.putRequest(queryParams, PGM_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessPgmPostService() {
		logger.info("TESTING Pgm User Access Pgm POST Service");
		/* 
		 * For POST service, we must create and delete the resource that was created
		 * NOTE: Using ADMIN_USER for portions of the test that require deleting resources.
		 *  
		 * */
		
		/* Delete the TEST pgm if already created */
		List<String[]> queryParamspre = new ArrayList<>();
		String[] namespre = new String[] {"name", "TEST"};
		queryParamspre.add(namespre);
		RequestUtils.deleteRequest(queryParamspre, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		/* Create the TEST pgm */
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "TEST"};
		queryParams.add(names);
		Response response = RequestUtils.postRequest(queryParams, PGM_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		CreateProgramOutput cpr = RequestUtils.validateCPRSuccess(response);
		
		/* Verifying if pgm resource was created */
		logger.info(cpr.toString());
		for (CreateProgram q : cpr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getPgm());
		}
		for (String key : cpr.getMessages().keySet()) {
			assertEquals(null, cpr.getMessages().get(key).getRc());
		}
		
		/* Deleting the TEST pgm */
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "TEST"};
		queryParams2.add(names2);
		Response response2 = RequestUtils.deleteRequest(queryParams2, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		DeleteProgramOutput dpr2 = RequestUtils.validateDPRSuccess(response2);
		
		/* Verifying if pgm resource was deleted */
		logger.info(dpr2.toString());
		for (DeleteProgram q : dpr2.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getPgm());
		}
		for (String key : dpr2.getMessages().keySet()) {
			assertEquals(null, dpr2.getMessages().get(key).getRc());
		}
	}
	
	@Test
	public void testPgmUserAccessTranGetService() {
		logger.info("TESTING Pgm User Access Tran GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), TRAN_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessTranPutService() {
		logger.info("TESTING Pgm User Access Tran PUT Service (stopping scheduling of a transaction)");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response response = RequestUtils.putRequest(queryParams, TRAN_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessTranPostService() {
		logger.info("TESTING Pgm User Access Tran POST Service");	
		// Will use ADMIN_USER for any necessary delete requests
		// Need to create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		// Attempt to create transaction
		logger.info("Attempting to create test transaction");
		List<String[]> queryParams1 = new ArrayList<>();
		String[] names1 = new String[] {"name", "TEST"};
		String[] pgm1 = new String[] {"pgm", "JUNIT"};
		queryParams1.add(names1);
		queryParams1.add(pgm1);
		Response response1 = RequestUtils.postRequest(queryParams1, TRAN_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response1.getStatus());
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	@Test
	public void testPgmUserAccessRegionGetService() {
		logger.info("TESTING Pgm User Access Region GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), REGION_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessRegionPutService() {
		logger.info("TESTING Pgm User Access Region PUT Service");
		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), REGION_PATH + "/stop", PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testRegionUserAccessRegionGetService() {
		logger.info("TESTING Region User Access Region GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), REGION_PATH, REGION_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testRegionUserAccessRegionPutService() {
		logger.info("TESTING Region User Access Region PUT Service");
		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), REGION_PATH + "/stop", REGION_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testRegionUserAccessPgmGetService() {
		logger.info("TESTING REGION User Access Pgm GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, REGION_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testRegionUserAccessPgmPutService() {
		logger.info("TESTING Region User Access Pgm PUT Service");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "DBF*"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		
		Response response = RequestUtils.putRequest(queryParams, PGM_PATH, REGION_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testRegionUserAccessPgmPostService() {
		logger.info("TESTING Region User Access Pgm POST Service");
		/* 
		 * For Pgm POST service, we must create and delete the resource that was created
		 * NOTE: Using ADMIN_USER for portions of the test that require deleting resources.
		 *  
		 * */
		
		/* Delete the TEST pgm if already created */
		List<String[]> queryParamspre = new ArrayList<>();
		String[] namespre = new String[] {"name", "TEST"};
		queryParamspre.add(namespre);
		RequestUtils.deleteRequest(queryParamspre, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		/* Attempt to create the TEST pgm */
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "TEST"};
		queryParams.add(names);
		Response response = RequestUtils.postRequest(queryParams, PGM_PATH, REGION_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testRegionUserAccessTranGetService() {
		logger.info("TESTING Region User Access Tran GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), TRAN_PATH, REGION_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testRegionUserAccessTranPutService() {
		logger.info("TESTING Region User Access Tran PUT Service (stopping scheduling of a transaction)");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response response = RequestUtils.putRequest(queryParams, TRAN_PATH, REGION_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testRegionUserAccessTranPostService() {
		logger.info("TESTING Region User Access Tran POST Service");	
		/* Will use ADMIN_USER for any necessary delete requests */
		
		// Need to create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		// Attempt to create transaction
		logger.info("Attempting to create test transaction");
		List<String[]> queryParams1 = new ArrayList<>();
		String[] names1 = new String[] {"name", "TEST"};
		String[] pgm1 = new String[] {"pgm", "JUNIT"};
		queryParams1.add(names1);
		queryParams1.add(pgm1);
		Response response1 = RequestUtils.postRequest(queryParams1, TRAN_PATH, REGION_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response1.getStatus());
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	@Test
	public void testGetUserAccessPgmGetService() {
		logger.info("TESTING Get User Access Pgm GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testGetUserAccessTranGetService() {
		logger.info("TESTING Get User Access Tran GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), TRAN_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testGetUserAccessRegionGetService() {
		logger.info("TESTING Get User Access Region GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), REGION_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testGetUserAccessPgmPostService() {
		logger.info("TESTING Get User Access Pgm POST Service");
		/* 
		 * For Pgm POST service, we must create and delete the resource that was created
		 * NOTE: Using ADMIN_USER for portions of the test that require deleting resources.
		 *  
		 * */
		
		/* Delete the TEST pgm if already created */
		List<String[]> queryParamspre = new ArrayList<>();
		String[] namespre = new String[] {"name", "TEST"};
		queryParamspre.add(namespre);
		RequestUtils.deleteRequest(queryParamspre, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		/* Attempt to create the TEST pgm */
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "TEST"};
		queryParams.add(names);
		Response response = RequestUtils.postRequest(queryParams, PGM_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testGetUserAccessTranPostService() {
		logger.info("TESTING Get User Access Tran POST Service");	
		// Will use ADMIN_USER for any necessary delete requests
		// Need to create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		// Attempt to create transaction
		logger.info("Attempting to create test transaction");
		List<String[]> queryParams1 = new ArrayList<>();
		String[] names1 = new String[] {"name", "TEST"};
		String[] pgm1 = new String[] {"pgm", "JUNIT"};
		queryParams1.add(names1);
		queryParams1.add(pgm1);
		Response response1 = RequestUtils.postRequest(queryParams1, TRAN_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response1.getStatus());
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	
	@Test
	public void testGetUserAccessPgmPutService() {
		logger.info("TESTING Get User Access Pgm PUT Service");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "DBF*"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		
		Response response = RequestUtils.putRequest(queryParams, PGM_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testGetUserAccessTranPutService() {
		logger.info("TESTING Get User Access Tran PUT Service (stopping scheduling of a transaction)");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response response = RequestUtils.putRequest(queryParams, TRAN_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testGetUserAccessRegionPutService() {
		logger.info("TESTING Get User Access Region PUT Service");
		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), REGION_PATH + "/stop", GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testGetUserAccessPgmDeleteService() {
		logger.info("TESTING Get User Access Pgm DELETE Service");	
		// Will use ADMIN_USER for any necessary delete requests
		// Create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		// Attempt to delete test program JUNIT
		logger.info("Attempting to delete test program 'JUNIT'");
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "JUNIT"};
		queryParams2.add(names2);
		Response response = RequestUtils.deleteRequest(queryParams2, PGM_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	@Test
	public void testGetUserAccessTranDeleteService() {
		logger.info("TESTING Get User Access Tran DELETE Service");	
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
		/*Check if data is correct*/
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
		Response response2 = RequestUtils.deleteRequest(queryParams2, TRAN_PATH, GET_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response2.getStatus());
		
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
	/*(
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
		
		// Attempt to delete test program JUNIT
		logger.info("Attempting to delete test program 'JUNIT'");
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "JUNIT"};
		queryParams2.add(names2);
		Response response = RequestUtils.deleteRequest(queryParams2, PGM_PATH, POST_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		
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
	}*/
	
 	
	public void testIncorrectCredentials() {
		
	}
	
	public void testCorrectUsernameIncorrectPassword() {
		
	}
	
	public void testIncorrectUsernameCorrectPassword() {
		
	}

	public void testRoles() {
		
	}
}
