package logicanalyser;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.stream.JsonGenerator;

@Named("visulizationbean")
@RequestScoped
public class VisuliationBean {
	@Inject
	private FileSessionBean session;
	private MetricSingle warningCountMetric;
	private MetricMap languageTypeMetric;
	//private MetricSingle criticalErrors;
	private List<Marker> listOfMarkers;
	private int errorCount;
	private int criticalErrorCount;
	private MetricList listOfIndentation;

	@PostConstruct
	private void initialise() {
		Report report = session.getReport();
		
		warningCountMetric = (MetricSingle)report.getMetric("warningcount");
		//criticalErrors = (MetricSingle)report.getMetric("errorcount");
		languageTypeMetric = (MetricMap) report.getMetric("languages");
		listOfMarkers = report.getAllMarkers();
		listOfIndentation = (MetricList) report.getMetric("indentation");

		for (Marker mkr: listOfMarkers) {
			switch (mkr.getSeverity()) {

			case Error:
				++errorCount;
				break;
			case CriticalError:
				++criticalErrorCount;
				break;
			default:
				break;
			}
		}
		}

	public String getJSONData() {
		MetricMap countMetric;
		Report report = session.getReport();
		if (report.hasMetric("html.tagcount")) {
			countMetric = (MetricMap)report.getMetric("html.tagcount");
		} else if (report.hasMetric("css.propertycount")) {
			countMetric = (MetricMap)report.getMetric("css.propertycount");
		} else {
			countMetric = new MetricMap("", Collections.emptyMap());
		}
		
		StringWriter writer = new StringWriter();
		JsonGenerator generator = Json.createGenerator(writer);

		generator.writeStartArray();
		for (String tagName : countMetric.getKeys()) {
			MetricValue value = countMetric.getValue(tagName);
			generator.writeStartObject();
			generator.write("name", tagName);
			generator.write("count", value.get(0));
			if (value.size() > 1) {
				generator.write("warnings", value.get(1));
				generator.write("errors", value.get(2));
			} else {
				generator.write("warnings", 0);
				generator.write("errors", 0);
			}
			generator.writeEnd();
		}
		generator.writeEnd();
		generator.flush();

		return writer.toString();
	}

	public int getWarningCount(){

		return warningCountMetric.getValue().get();



	}
	
	public String getIndentation() {
	
		StringWriter writer = new StringWriter();
		JsonGenerator generator = Json.createGenerator(writer);
		String[] colorArray= new String[] {"#0a1829", "#588e95", "#7fada9", "#ccd9ce"};
		generator.writeStartArray();
		int constant= 100;
		for (MetricValue val: listOfIndentation.getValues()) {
			generator.writeStartObject();
			
			generator.write("low",val.get(0));
			generator.write("high", Math.min(constant, val.get(1)));
			
			generator.write("color", colorArray[val.get(2)]);
			generator.writeEnd();
		}
		generator.writeEnd();
		generator.flush();

		return writer.toString();
		
	}

	public int getErrorCount() {
		return errorCount;
	}

	public int getCriticalErrorCount() {

	return criticalErrorCount;

	}

	/**
	 *
	 * @return
	 */
	public String getJSONLangData() {
		StringWriter writer = new StringWriter();
		JsonGenerator generator = Json.createGenerator(writer);

		generator.writeStartArray();
		for (String name : languageTypeMetric.getKeys()) {
			generator.writeStartObject();
			generator.write("name", name);
			generator.writeStartArray("data");
			generator.write(languageTypeMetric.getValue(name).get());
			generator.writeEnd();
			switch(name.toLowerCase()){
			case "html": 
				generator.write("color","#0a1829");
				break;
			case "css": 
				generator.write("color","#588e95");
				break;
			case "javascript": 
				generator.write("color","#7fada9");
				break;
			default: 
				generator.write("color","#ccd9ce");
				break;
				
				
			}
			generator.writeEnd();
		}
		generator.writeEnd();
		
		generator.flush();

		return writer.toString();
	}
}
