package logicanalyser;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("errorList")
@RequestScoped
public class ErrorListBean {
	@Inject
	private FileSessionBean session;
	
	@PostConstruct
	private void initialize() {
		
	}
	
	public List<Marker> getMarkers() {
		return session.getReport().getAllMarkers();
	}
	
	public int toLineNumber(Interval location) {
		String[] lines = session.getLines();
		
		int count = 0;
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			count += line.length() + 1;
			if (location.start < count) {
				return i + 1;
			}
		}
		
		// Shouldnt happen
		return 1;
	}
	
	public int toColumnNumber(Interval location) {
		String[] lines = session.getLines();
		
		int count = 0;
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			if (location.start < count + line.length()) {
				return location.start - count + 1;
			}
			
			count += line.length() +  1;
		}
		
		// Shouldnt happen
		return 1;
	}
}
