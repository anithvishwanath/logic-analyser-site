package logicanalyser.site;

import java.util.Set;

import javax.ejb.Local;

import com.google.common.net.MediaType;

import logicanalyser.InvalidContentException;
import logicanalyser.LogicAnalyser;
import logicanalyser.Report;

@Local
public interface Analyser {
	/**
	 * @see LogicAnalyser#canAnalyseContent(MediaType)
	 */
	boolean canHandle(MediaType type);
	
	/**
	 * @see LogicAnalyser#getSupportedTypes()
	 */
	Set<MediaType> getSupportedTypes();
	
	/**
	 * Gets the name of the language that the MIME type
	 * is for.
	 * @param type The MIME type
	 * @return The name of the language
	 */
	String getLanguage(MediaType type);

	/**
	 * @see LogicAnalyser#analyseContent(MediaType, String)
	 */
	Report analyse(MediaType type, String content);
}
