
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package zowe.mc.tests.tran;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.tran.query.QueryTransaction;
import application.rest.responses.tran.query.QueryTransactionOutput;
import application.rest.responses.tran.update.UpdateTransactionOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for "UPDATE TRAN" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestUpdateTran {
	private static final Logger logger = LoggerFactory.getLogger(TestUpdateTran.class);
	
	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
	}

	/**
	 * Tests rest service for submitting UPDATE TRAN IMS command
	 * @throws Exception
	 */
	@Test
	public void testUpdateTranStopSchd() {

		//First we stop the transaction
		logger.info("Testing Update TRAN by stopping scheduling of a transaction");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response responses = RequestUtils.putRequest(queryParams, "/" + TestProperties.plex + "/transaction");
		UpdateTransactionOutput upr = RequestUtils.validateUTRSuccess(responses);
		logger.info(upr.toString());
		
		//Then we verify it's status
		logger.info("Verifying status");	
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "JUNIT"};
		String[] show = new String[] {"attributes", "STATUS"};
		queryParams2.add(names2);
		queryParams2.add(show);
		Response responses2 = RequestUtils.getRequest(queryParams2, "/" + TestProperties.plex + "/transaction");
		QueryTransactionOutput qpr = RequestUtils.validateQTRSuccess(responses2);
		for (QueryTransaction r : qpr.getData()) {
			assertEquals("STOSCHD", r.getLstt());
		}
		
		//First we start the transaction
		logger.info("Testing Update TRAN by starting scheduling of a transaction");		
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		String[] start = new String[] {"start", "SCHD"};
		queryParams3.add(names3);
		queryParams3.add(start);
		Response responses3 = RequestUtils.putRequest(queryParams3, "/" + TestProperties.plex + "/transaction");
		UpdateTransactionOutput upr2 = RequestUtils.validateUTRSuccess(responses3);
		logger.info(upr2.toString());
		
		//Then we verify it's status
		logger.info("Verifying status");	
		List<String[]> queryParams4 = new ArrayList<>();
		String[] names4 = new String[] {"name", "JUNIT"};
		String[] show2 = new String[] {"attributes", "STATUS"};
		queryParams4.add(names4);
		queryParams4.add(show2);
		Response responses4 = RequestUtils.getRequest(queryParams4, "/" + TestProperties.plex + "/transaction");
		QueryTransactionOutput qpr2 = RequestUtils.validateQTRSuccess(responses4);
		for (QueryTransaction r : qpr2.getData()) {
			assertEquals(null, r.getLstt());
		}
		
		
	}
	
	

}

