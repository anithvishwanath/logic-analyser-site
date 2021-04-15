package logicanalyser;


import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Handles session to transfer data from file upload page
 * to the display report page.
 *
 * A simple data container for all the variables in Report.
 *
 */
@Named
@SessionScoped
public class FileSessionBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Report report;
	private String fileContents;
	private String[] lines;
	private String language;
	
	/**
	 * Sets the report
	 * @param report
	 */
	public void setReport(Report report) {
		this.report = report;
	}

	/**
	 * Gets the report
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}

	/**
	 * Gets file contents.
	 * @return the fileContents
	 */
	public String getFileContents() {
		return fileContents;
	}

	/**
	 * Set the file contents.
	 * @param fileContents the fileContents to set
	 */
	public void setFileContents(String fileContents) {
		this.fileContents = fileContents;
		lines = fileContents.split("\n");
	}
	
	public String[] getLines() {
		return lines;
	}
	
	public boolean isValid() {
		return report != null && fileContents != null;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getLanguage() {
		return language;
	}
}