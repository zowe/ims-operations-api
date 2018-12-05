package zowe.mc;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;
//import org.junit.runners.Suite;
//import org.junit.runners.Suite.SuiteClasses;

import zowe.mc.tests.TestOMConnection;
import zowe.mc.tests.TestQueryPgm;

@RunWith(JUnitPlatform.class)
@SelectClasses( { TestOMConnection.class, TestQueryPgm.class } )
public class MCSuiteTest {

}
