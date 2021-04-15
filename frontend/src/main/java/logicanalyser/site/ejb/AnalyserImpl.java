package logicanalyser.site.ejb;

import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.google.common.net.MediaType;

import logicanalyser.InvalidContentException;
import logicanalyser.LogicAnalyser;
import logicanalyser.Report;
import logicanalyser.languages.LanguageBase;
import logicanalyser.site.Analyser;

@Stateless
public class AnalyserImpl implements Analyser {
	@Inject
	private LibraryConnector connector;

	/**
	 * @see LogicAnalyser#canAnalyseContent(MediaType)
	 */
	@Override
	public boolean canHandle(MediaType type) {
		return connector.get().canAnalyseContent(type);
	}
	
	/**
	 * @see LogicAnalyser#getSupportedTypes()
	 */
	@Override
	public Set<MediaType> getSupportedTypes() {
		return connector.get().getSupportedTypes();
	}
	
	@Override
	public String getLanguage(MediaType type) {
		LanguageBase lang = connector.get().getLanguageFor(type);
		if (lang != null) {
			return lang.getName();
		} else {
			return "";
		}
	}

	/**
	 * @see LogicAnalyser#analyseContent(MediaType, String)
	 */
	@Override
	public Report analyse(MediaType type, String content) {
		try {
			return connector.get().analyseContent(type, content);
		} catch (InvalidContentException e) {
			return null;
		}
	}
}