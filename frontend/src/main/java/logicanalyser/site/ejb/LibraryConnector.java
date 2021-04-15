package logicanalyser.site.ejb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

import logicanalyser.LogicAnalyser;
import logicanalyser.Ruleset;
import logicanalyser.config.RuleConfiguration;
import logicanalyser.config.XMLConfigLoader;
import logicanalyser.languages.*;
import logicanalyser.site.startup.AddonLoader;
import rulesets.css.AllCSSRulesets;
import rulesets.html.AllHTMLRulesets;

@Singleton
@LocalBean
public class LibraryConnector {
	private LogicAnalyser analyser;

	private Logger log;
	private File configDir;
	
	@PostConstruct
	private void construct() {
		log = Logger.getLogger("WebAnalyser");
		
		analyser = new LogicAnalyser();
	}
	
	public void load() {
		loadConfigDir();
		
		analyser.applyRuleConfiguration(loadMessageConfig());
		
		loadInternalAddons();
		loadAddons();
	}
	
	public LogicAnalyser get() {
		return analyser;
	}
	
	private void loadConfigDir() {
		String setting = System.getProperty("webanalyser.config.dir");
		if (setting != null) {
			configDir = new File(setting);
		} else {
			File wildFlyDir = new File(System.getProperty("jboss.server.config.dir"));
			
			configDir = new File(wildFlyDir, "webanalyser/");
		}
		
		if (!configDir.exists() && !configDir.mkdirs()) {
			log.fatal("Failed to create configuration directory: " + configDir);
		}
	}
	
	private RuleConfiguration loadMessageConfig() {
		File messagesFile = new File(configDir, "messages.xml");
		XMLConfigLoader loader = null;
		if (messagesFile.exists()) {
			try {
				loader = new XMLConfigLoader(messagesFile);
				log.info("Loading messages from " + messagesFile);
			} catch (FileNotFoundException e) {
				// Should not happen
				log.fatal("Internal error", e);
			}
		}
		
		if (loader == null) {
			InputStream stream = LibraryConnector.class.getResourceAsStream("/messages.xml");
			if (stream != null) {
				loader = new XMLConfigLoader(stream);
				log.info("The fallback messages.xml will be used");
			} else {
				log.warn("No fallback messages.xml was found. Default messages will be used");
				return null;
			}
		}
		
		try {
			return loader.load();
		} catch (IOException e) {
			log.error("Failed to read " + messagesFile, e);
		} catch (SAXException e) {
			log.error("There is an error in your messages.xml file. ", e);
		}
		
		return null;
	}
	
	private void loadAddons() {
		File dir = new File(configDir, "addons");
		if (!dir.exists()) {
			return; // TODO: Load internal content
		}
		
		Map<String, List<Ruleset>> rulesets = Maps.newHashMap();
		
		for (File file : dir.listFiles()) {
			if (!file.getName().endsWith(".jar")) {
				continue;
			}
			
			AddonLoader loader = new AddonLoader(file);
			
			try {
				loader.load();
				
				loadLanguages(loader.getLanguages(), loader.getFileName());
				rulesets.put(loader.getFileName(), loader.getRulesets());
			} catch (IOException e) {
				log.error("Failed to access addon " + file.getName() + ". IO Error", e);
			}
		}
		
		for (String fileName : rulesets.keySet()) {
			loadRulesets(rulesets.get(fileName), fileName);
		}
	}
	
	private void loadInternalAddons() {
		AllHTMLRulesets.installInto(analyser);
		AllCSSRulesets.installInto(analyser);
	}
	
	private void loadLanguages(List<LanguageBase> languages, String file) {
		for (LanguageBase language : languages) {
			try {
				analyser.registerLanguageProcessor(language);
				log.info("Installed language " + language.getName() + " from " + file);
			} catch (IllegalArgumentException e) {
				log.error("Duplicate language " + language.getName() + " in " + file);
			}
		}
	}
	
	private void loadRulesets(List<Ruleset> rulesets, String file) {
		for (Ruleset ruleset : rulesets) {
			try {
				analyser.registerRuleset(ruleset);
				log.info("Installed ruleset " + ruleset.getCategoryName() + " from " + file);
			} catch (IllegalStateException e) {
				log.error("Ruleset " + ruleset.getCategoryName() + " from " + file + " is missing the language " + ruleset.getLanguage().getSimpleName());
			}
		}
	}
}
