//package zowe.mc.tests;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.ws.rs.core.Response;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import application.rest.responses.tran.create.CreateTransaction;
//import application.rest.responses.tran.create.CreateTransactionOutput;
//import application.rest.responses.tran.delete.DeleteTransaction;
//import application.rest.responses.tran.delete.DeleteTransactionOutput;
//import zowe.mc.RequestUtils;
//import zowe.mc.SuiteExtension;
//import zowe.mc.TestProperties;
//
//
//
//@ExtendWith({SuiteExtension.class})
//public class TestBasicAuthenticationRegionUser {
//
//	private static final Logger logger = LoggerFactory.getLogger(TestBasicAuthenticationRegionUser.class);
//	private static final String ADMIN_USER = "admin";
//	private static final String REGION_USER = "region";
//	private static final String DELETE_USER = "delete";
//	private static final String DEFAULT_PASSWORD = "password";
//	private static String PGM_PATH = TestProperties.contextPath + TestProperties.plex + "/program";
//	private static String TRAN_PATH = TestProperties.contextPath + TestProperties.plex + "/transaction";
//	private static String REGION_PATH = TestProperties.contextPath + TestProperties.plex + "/region";
//
//	/**
//	 * Setup rest client
//	 */
//	@BeforeAll
//	public static void setUp() {
//	}
//	
//	@Test
//	public void testRegionUserAccessRegionGetService() {
//		logger.info("TESTING Region User Access Region GET Service");
//		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), REGION_PATH, REGION_USER, DEFAULT_PASSWORD);
//		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//	}
//	
//	@Test
//	public void testRegionUserAccessRegionPutService() {
//		logger.info("TESTING Region User Access Region PUT Service");
//		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), REGION_PATH + "/stop", REGION_USER, DEFAULT_PASSWORD);
//		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//	}
//	
//	@Test
//	public void testRegionUserAccessPgmGetService() {
//		logger.info("TESTING REGION User Access Pgm GET Service");
//		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, REGION_USER, DEFAULT_PASSWORD);
//		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
//	}
//	
//	@Test
//	public void testRegionUserAccessPgmPutService() {
//		logger.info("TESTING Region User Access Pgm PUT Service");
//		
//		List<String[]> queryParams = new ArrayList<>();
//		String[] names = new String[] {"name", "DBF*"};
//		String[] stop = new String[] {"stop", "SCHD"};
//		queryParams.add(names);
//		queryParams.add(stop);
//		
//		Response response = RequestUtils.putRequest(queryParams, PGM_PATH, REGION_USER, DEFAULT_PASSWORD);
//		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
//	}
//	
//	@Test
//	public void testRegionUserAccessPgmPostService() {
//		logger.info("TESTING Region User Access Pgm POST Service");
//		/* 
//		 * NOTE: Using ADMIN_USER for portions of the test that require deleting resources. 
//		 * */
//		
//		/* Delete the TEST pgm if already created */
//		List<String[]> queryParamspre = new ArrayList<>();
//		String[] namespre = new String[] {"name", "TEST"};
//		queryParamspre.add(namespre);
//		RequestUtils.deleteRequest(queryParamspre, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
//		
//		/* Attempt to create the TEST pgm */
//		List<String[]> queryParams = new ArrayList<>();
//		String[] names = new String[] {"name", "TEST"};
//		queryParams.add(names);
//		Response response = RequestUtils.postRequest(queryParams, PGM_PATH, REGION_USER, DEFAULT_PASSWORD);
//		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
//	}
//	
//	@Test
//	public void testRegionUserAccessTranGetService() {
//		logger.info("TESTING Region User Access Tran GET Service");
//		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), TRAN_PATH, REGION_USER, DEFAULT_PASSWORD);
//		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
//	}
//	
//	@Test
//	public void testRegionUserAccessTranPutService() {
//		logger.info("TESTING Region User Access Tran PUT Service (stopping scheduling of a transaction)");		
//		List<String[]> queryParams = new ArrayList<>();
//		String[] names = new String[] {"name", "JUNIT"};
//		String[] stop = new String[] {"stop", "SCHD"};
//		queryParams.add(names);
//		queryParams.add(stop);
//		Response response = RequestUtils.putRequest(queryParams, TRAN_PATH, REGION_USER, DEFAULT_PASSWORD);
//		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
//	}
//	
//	@Test
//	public void testRegionUserAccessTranPostService() {
//		logger.info("TESTING Region User Access Tran POST Service");	
//		/* Will use ADMIN_USER for any necessary delete requests */
//		
//		// Need to create a program first
//		logger.info("Creating test program 'JUNIT'");
//		List<String[]> queryParams = new ArrayList<>();
//		String[] names = new String[] {"name", "JUNIT"};
//		queryParams.add(names);
//		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
//		
//		// Attempt to create transaction
//		logger.info("Attempting to create test transaction");
//		List<String[]> queryParams1 = new ArrayList<>();
//		String[] names1 = new String[] {"name", "TEST"};
//		String[] pgm1 = new String[] {"pgm", "JUNIT"};
//		queryParams1.add(names1);
//		queryParams1.add(pgm1);
//		Response response1 = RequestUtils.postRequest(queryParams1, TRAN_PATH, REGION_USER, DEFAULT_PASSWORD);
//		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response1.getStatus());
//		
//		// Delete program that was created
//		logger.info("Deleting test program 'JUNIT'");
//		List<String[]> queryParams3 = new ArrayList<>();
//		String[] names3 = new String[] {"name", "JUNIT"};
//		queryParams3.add(names3);
//		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
//	}
//	
//	
//	@AfterEach
//	public void sit() throws InterruptedException {
//		Thread.sleep(2000);
//	}
//	
//	
//}
