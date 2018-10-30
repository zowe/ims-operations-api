package zowe.mc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icon.helpers.MCInteraction;
import om.connection.IconOmConnection;
import om.connection.IconOmConnectionFactory;
import om.exception.OmConnectionException;

public class ConnectionTest {

	private static MCInteraction mcSpec = new MCInteraction();
	private static final Logger logger = LoggerFactory.getLogger(ConnectionTest.class);

	@BeforeAll
	public static void setUp() {
		mcSpec.setHostname("ec32016a.vmec.svl.ibm.com");
		mcSpec.setPort(9999);
		mcSpec.setImsPlexName("IM00P");
		mcSpec.getDatastores().add("IMS1");
		mcSpec.getDatastores().add("IMS2");
	}

	@Test
	public void testImsConnection() throws Exception
	{
		logger.info("TESTING MC Connection");
		try {

			if (logger.isDebugEnabled()) logger.debug("Creating connection to " + mcSpec.getHostname() + ":" + mcSpec.getPort());

			IconOmConnectionFactory IconCF = new IconOmConnectionFactory();
			IconOmConnection omConnection = IconCF.createIconOmConnectionFromData(mcSpec);
			assertFalse(omConnection.isErrorInConnection());

			if (logger.isDebugEnabled()) logger.debug("Connection Successful!"); 
		} catch (OmConnectionException e) {
			logger.error("OmConnectionException", e);
			fail("Connection unsuccessful");
		}

	}
}
