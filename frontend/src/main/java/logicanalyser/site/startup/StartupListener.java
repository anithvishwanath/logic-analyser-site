package logicanalyser.site.startup;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;
import logicanalyser.site.ejb.LibraryConnector;

@WebListener("WebAnalyser-Startup")
public class StartupListener implements ServletContextListener {
	@Inject
	private LibraryConnector connector;
	private Logger log;
	
	public StartupListener() {
		log = Logger.getLogger("WebAnalyser");
	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Starting up WebAnalyser...");
		
		connector.load();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
