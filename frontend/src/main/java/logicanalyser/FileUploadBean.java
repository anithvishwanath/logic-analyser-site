package logicanalyser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;

import logicanalyser.site.Analyser;

/**
 * Handles the file upload functionality.
 *
 */
@Named
@RequestScoped
public class FileUploadBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private Analyser analyser;
	@Inject
	private FileSessionBean fileSessionBean;

	private Part file; // Handle the uploaded file from the form.
	private String fileContent; // Manage contents present in the file.

	
	public String postUpload() {
		return "/view/view.xhtml?faces-redirect=true";
	}
	
	public void upload(ActionEvent event) throws AbortProcessingException {
		FacesContext context = FacesContext.getCurrentInstance();
		UIComponent component = event.getComponent();
		
		try {
			String content = CharStreams.toString(new InputStreamReader(file.getInputStream()));
			MediaType type = MediaType.parse(file.getContentType());
			Report report = analyser.analyse(type, content);
			
			if (report == null) {
				FacesMessage message = new FacesMessage("Please choose a non-empty file");
				context.addMessage(component.getClientId(), message);
				
				throw new AbortProcessingException();
			}
			
			fileSessionBean.setFileContents(content);
			fileSessionBean.setReport(report);
			fileSessionBean.setLanguage(analyser.getLanguage(type));
		} catch (IOException | IllegalArgumentException e) {
			FacesMessage message = new FacesMessage("Sorry, something went wrong while analysing your file");
			context.addMessage(component.getClientId(), message);
			
			throw new AbortProcessingException(e);
		}
	}
	
	/**
	 * Handles validation for the input file component of the HTML form.
	 * @param context processes the request
	 * @param component checks for correctness and renders error messages (if any)
	 * @param value used to validate the file contents from the form
	 *
	 * Referred from:
	 * http://javaonlineguide.net/2015/08/jsf-2-2-hinputfile-fvalidator-tag-example-for-file-upload-validate.html
	 */
	public void validateFile(FacesContext context, UIComponent component, Object value) {
		List<FacesMessage> validationMessage = new ArrayList<>();

		Part file = (Part) value;
		if (file == null) {
			validationMessage.add(new FacesMessage("Please choose a file."));
		} else if (file.getSize() <= 0 || file.getContentType().isEmpty()) {
			validationMessage.add(new FacesMessage("Please choose a non-empty file"));
		} else if (file.getSize() > 2000000) {
			validationMessage.add(new FacesMessage("Please upload files less than 2MB."));
		} else {
			try {
				MediaType type = MediaType.parse(file.getContentType());
				if (!analyser.canHandle(type)) {
					validationMessage.add(new FacesMessage("That file is not supported by the analyser."));
				}
			} catch (IllegalArgumentException e) { 
				validationMessage.add(new FacesMessage("That file is not supported by the analyser."));
			}
		}
		if (!validationMessage.isEmpty()) {
			throw new ValidatorException(validationMessage);
		}
	}
	
	public String getUploadAccept() {
		StringBuilder builder = new StringBuilder();
		
		boolean first = true;
		for (MediaType type : analyser.getSupportedTypes()) {
			if (!first) {
				builder.append(',');
			}
			builder.append(type.withoutParameters().toString());
			first = false;
		}
		
		return builder.toString();
	}
	
	/**
	 * Sets the file to be uploaded.
	 * @param file to be uploaded
	 */
	public void setFile(Part file) {
		this.file = file;
	}

	/**
	 * Gets the file.
	 * @return the file
	 */
	public Part getFile() {
		return file;
	}

	/**
	 * Get the content in the file uploaded.
	 * @return the file content
	 */
	public String getFileContent() {
		return fileContent;
	}
}
