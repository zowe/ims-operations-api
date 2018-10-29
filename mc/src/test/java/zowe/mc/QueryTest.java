package zowe.mc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icon.helpers.MCInteraction;
import zowe.mc.servlet.OMServlet;


public class QueryTest 
{

	private static final Logger logger = LoggerFactory.getLogger(QueryTest.class);
	private static MCInteraction mcSpec = new MCInteraction();
	private OMServlet omServlet = new OMServlet();
	private static WebTarget webTarget = null;
	
	private static final String usrDir = "/Users/jerryli/Documents/wlp";
	private static final String serverName = "TMRADevServer";

	@BeforeAll
	public static void setUp() {
		mcSpec.setHostname("ec32016a.vmec.svl.ibm.com");
		mcSpec.setPort(9999);
		mcSpec.setImsPlexName("IM00P");
		mcSpec.getDatastores().add("IMS1");
		mcSpec.getDatastores().add("IMS2");

		Client client = ClientBuilder.newClient();
		webTarget = client.target("http://localhost:9080/mc/services/");
		
		
		
//		//Start Liberty
//		try {
//			Process proc = Runtime.getRuntime().exec("java -javaagent:"+ usrDir +"/bin/tools/ws-javaagent.jar -jar " + usrDir + "/bin/tools/ws-server.jar TMRADevServer");
////			Scanner s = new Scanner(proc.getInputStream()).useDelimiter("\\A");
////			String line = "";
////			while(s.hasNextLine()) {
////				line=s.nextLine();
////				if (line.contains(serverName + " is ready to run a smarter planet")) {
////					System.out.println("TMRA started");
////					proc.waitFor(1000, TimeUnit.NANOSECONDS);
////				}
////			}
//			Thread.sleep(8000);
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

//	public void testConnection() throws Exception
//	{
//		logger.info("TESTING MC Connection");
//		try {
//
//			if (logger.isDebugEnabled()) logger.debug("Creating connection to " + mcSpec.getHostname() + ":" + mcSpec.getPort());
//
//			IconOmConnectionFactory IconCF = new IconOmConnectionFactory();
//			IconOmConnection omConnection = IconCF.createIconOmConnectionFromData(mcSpec);
//			assertFalse(omConnection.isErrorInConnection());
//
//			if (logger.isDebugEnabled()) logger.debug("Connection Successful!"); 
//		} catch (OmConnectionException e) {
//			logger.error("OmConnectionException", e);
//			fail("Connection unsuccessful");
//		}
//
//	}

	@Test
	public void testQuery() throws Exception {
		String path = "/Query/PGM";
		
		Invocation.Builder invocationBuilder =  webTarget.path(path).request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.header("hostname", "ec32016a.vmec.svl.ibm.com")
												.header("port", "9999")
												.header("plex", "IM00P").get();
		System.out.println("getting response");
		
		assertEquals(200, response.getStatus());
		
	}

//	public void testGetPGM() throws Exception
//	{
//		logger.info("TESTING MC get pgm");
//		String userCommand = "QUERY PGM NAME(*) SHOW(ALL)";
//		JSONObject result = omServlet.executeUserImsCommand(userCommand, mcSpec);
//		JSONObject message = (JSONObject) result.get("message");
//		JSONObject omMessageContext = (JSONObject) message.get("omMessageContext");
//		JSONArray executeUserCommand = (JSONArray) omMessageContext.get("executeUserImsCommand");
//		JSONObject messageTitle = (JSONObject) executeUserCommand.get(0);
//		String text = (String) messageTitle.get("status");
//		assertTrue(text.equals("success"));
//
//	}
//
//	public void testGetRegions() throws Exception {
//		logger.info("TESTING MC get regions");
//		String userCommand = "(DISPLAY ACT REGION) OPTION=AOPOUTPUT";
//		JSONObject result = omServlet.executeUserImsCommand(userCommand, mcSpec);
//		JSONObject message = (JSONObject) result.get("message");
//		JSONObject omMessageContext = (JSONObject) message.get("omMessageContext");
//		JSONArray executeUserCommand = (JSONArray) omMessageContext.get("executeUserImsCommand");
//		JSONObject messageTitle = (JSONObject) executeUserCommand.get(0);
//		String text = (String) messageTitle.get("status");
//		assertTrue(text.equals("success"));
//
//	}

//	@AfterAll
//	public static void cleanUp() {
//		try {
//			Thread.sleep(10000);
//			Process proc = Runtime.getRuntime().exec("java -javaagent:"+ usrDir +"/bin/tools/ws-javaagent.jar -jar " + usrDir + "/bin/tools/ws-server.jar TMRADevServer --stop");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
//	public void testHTTPHeader() throws Exception {
//		String path = "Query/PGM";
//		Invocation.Builder invocationBuilder =  webTarget.path(path).request(MediaType.APPLICATION_JSON);
//		Response response = invocationBuilder.get();
//		JSONObject json = response.readEntity(JSONObject.class);
//		response.close();
//
//	}
}
