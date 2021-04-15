package logicanalyser;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

@ManagedBean
@RequestScoped
public class DashboardBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private FileSessionBean sessionBean;

	private int lineCount;
	private int uniqueCount;
	private int errorCount;
	private int warningCount;

	@PostConstruct
	private void initialize() {
		Report report = sessionBean.getReport();

		MetricSingle metricLineCount = (MetricSingle) report.getMetric("linecount");
		MetricSingle metricErrorCount = (MetricSingle) report.getMetric("errorcount");
		MetricSingle metricWarningCount = (MetricSingle) report.getMetric("warningcount");

		this.lineCount = metricLineCount.getValue().get();
		this.errorCount = metricErrorCount.getValue().get();
		this.warningCount = metricWarningCount.getValue().get();
		
		if (report.hasMetric("html.tagcount")) {
			MetricMap tagCount = (MetricMap)report.getMetric("html.tagcount");
			uniqueCount = tagCount.size();
		} else if (report.hasMetric("css.propertycount")) {
			MetricMap propertyCount = (MetricMap)report.getMetric("css.propertycount");
			uniqueCount = propertyCount.size();
		}
	}

	public int getLineCount() {
		return lineCount;
	}

	public int getUniqueCount() {
		return uniqueCount;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public int getWarningCount() {
		return warningCount;
	}
	
	public String getLanguage() {
		return sessionBean.getLanguage();
	}
}
