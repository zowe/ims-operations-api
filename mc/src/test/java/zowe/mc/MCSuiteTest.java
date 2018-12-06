package zowe.mc;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;
//import org.junit.runners.Suite;
//import org.junit.runners.Suite.SuiteClasses;

import zowe.mc.tests.TestOMConnection;
import zowe.mc.tests.pgm.TestCreateDeletePgm;
import zowe.mc.tests.pgm.TestQueryPgm;
import zowe.mc.tests.pgm.TestStartPgm;
import zowe.mc.tests.pgm.TestUpdatePgm;
import zowe.mc.tests.tran.TestQueryTran;

@RunWith(JUnitPlatform.class)
@SelectClasses( { TestOMConnection.class, 
				  TestQueryPgm.class , 
				  TestStartPgm.class, 
				  TestUpdatePgm.class,
				  TestCreateDeletePgm.class,
				  TestQueryTran.class} )
public class MCSuiteTest {

}
