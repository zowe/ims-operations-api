
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package zowe.mc.tests.pgm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.pgm.start.StartProgramOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for "START PGM" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestStartPgm {
	
	private static final Logger logger = LoggerFactory.getLogger(TestStartPgm.class);


	@BeforeAll
	public static void setUp() {
	}
	
	/**
	 * Tests rest service for submitting START PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testStartPgm() {
		logger.info("TESTING START PGM");
		
		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), "/" + TestProperties.plex + "/program/start");
		StartProgramOutput spr = RequestUtils.validateSPRSuccess(response);
		logger.info(spr.toString());
		for (String key : spr.getMessages().keySet()) {
			assertEquals(null, spr.getMessages().get(key).getRc());
		}

	}
}
