package logicanalyser;

import java.io.StringWriter;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.stream.JsonGenerator;

@Named
@RequestScoped
public class FileViewBean {
	@Inject
	private FileSessionBean fileViewSession ;
	
	public String getFile() {
		return fileViewSession.getFileContents();
	}
	
	public List<Marker> getMarkers() {
		return fileViewSession.getReport().getAllMarkers();
	}
	
	public int getMarkerID(Marker marker) {
		return getMarkers().indexOf(marker);
	}
	
	public String getJSONData() {
		StringWriter writer = new StringWriter();
		JsonGenerator generator = Json.createGenerator(writer);
		
		generator.writeStartArray();
		
		Report report = fileViewSession.getReport();
		int id = 0;
		for (Marker marker : report.getAllMarkers()) {
			generator.writeStartObject();
			
			generator.write("id", id);
			switch (marker.getSeverity()) {
			case Informational:
				generator.write("type", 0);
				break;
			case Warning:
				generator.write("type", 1);
				break;
			case Error:
				generator.write("type", 2);
				break;
			case CriticalError:
				generator.write("type", 3);
				break;
			}
			generator.write("start", marker.getLocation().start);
			generator.write("end", marker.getLocation().end);
			
			generator.writeEnd();
			++id;
		}
		
		generator.writeEnd();
		
		generator.close();
		return writer.toString();
	}
	
	public int toLineNumber(Interval location) {
		String[] lines = fileViewSession.getLines();
		
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
	
	public String getLanguage() {
		return fileViewSession.getLanguage().toLowerCase();
	}
	
	public int getWarningCount() {
		return fileViewSession.getReport().getWarningMarkers().size();
	}
	
	public int getErrorCount() {
		return fileViewSession.getReport().getAllMarkers(SeverityRating.Error).size();
	}
	
	public int getCriticalCount() {
		return fileViewSession.getReport().getAllMarkers(SeverityRating.CriticalError).size();
	}
	
	public int getInfoCount() {
		return fileViewSession.getReport().getInfoMarkers().size();
	}
}
