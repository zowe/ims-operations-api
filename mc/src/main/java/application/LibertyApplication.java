package application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/services/*")
public class LibertyApplication extends Application{

}
