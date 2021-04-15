package logicanalyser.site.startup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import logicanalyser.Ruleset;
import logicanalyser.languages.LanguageBase;

public class AddonLoader {
	private final File jarFile;
	
	private final List<Ruleset> rulesets;
	private final List<LanguageBase> languages;
	
	private final Logger log = Logger.getLogger("WebAnalyser");
	
	public AddonLoader(File jarFile) {
		Preconditions.checkArgument(jarFile.exists());
		Preconditions.checkArgument(jarFile.getName().endsWith(".jar"));
		
		this.jarFile = jarFile;
		rulesets = Lists.newArrayList();
		languages = Lists.newArrayList();
	}
	
	public List<Ruleset> getRulesets() {
		return rulesets;
	}
	
	public List<LanguageBase> getLanguages() {
		return languages;
	}
	
	public String getFileName() {
		return jarFile.getName();
	}
	
	public void load() throws IOException {
		URL path;
		try {
			path = jarFile.toURI().toURL();
		} catch (MalformedURLException e) {
			// Should always be a valid URL
			throw new AssertionError(e);
		}
		
		URLClassLoader classLoader = new URLClassLoader(
			new URL[] {path}, 
			AddonLoader.class.getClassLoader()
		);
		
		List<String> classNames = getClasses();
		discoverTypes(classNames, classLoader);
	}
	
	private List<String> getClasses() throws IOException {
		try (JarFile jar = new JarFile(jarFile)) {
			
			List<String> classes = Lists.newArrayList();
			
			Enumeration<JarEntry> it = jar.entries();
			while (it.hasMoreElements()) {
				JarEntry entry = it.nextElement();
				if (!entry.getName().endsWith(".class")) {
					continue;
				}
				
				String className = entry.getName();
				className = className.substring(0, className.length() - 6);
				className = className.replace('/', '.');
				
				classes.add(className);
			}
			
			return classes;
		}
	}
	
	private void discoverTypes(List<String> classNames, ClassLoader classLoader) {
		for (String className : classNames) {
			try {
				Class<?> clazz = classLoader.loadClass(className);
				
				if (Ruleset.class.isAssignableFrom(clazz)) {
					Ruleset ruleset = loadRuleset(clazz.asSubclass(Ruleset.class));
					if (ruleset != null) {
						rulesets.add(ruleset);
					}
				}
				
				if (LanguageBase.class.isAssignableFrom(clazz)) {
					LanguageBase language = loadLanguage(clazz.asSubclass(LanguageBase.class));
					if (language != null) {
						languages.add(language);
					}
				}
			} catch (ClassNotFoundException e) {
				System.err.println("Could not find class: " + className);
			}
		}
	}
	
	private Ruleset loadRuleset(Class<? extends Ruleset> type) {
		try {
			Constructor<? extends Ruleset> constructor = type.getDeclaredConstructor();
			
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		} catch (NoSuchMethodException e) {
			log.fatal("Could not load ruleset " + type.getName() + " in " + getFileName() + ". No zero-args constructor available.");
		} catch (SecurityException e) {
			log.fatal("Could not load ruleset " + type.getName() + " in " + getFileName() + ". Security Error", e);
		} catch (InstantiationException e) {
			log.fatal("Could not load ruleset " + type.getName() + " in " + getFileName() + ". Failed to instantiate", e);
		} catch (InvocationTargetException e) {
			log.fatal("Could not load ruleset " + type.getName() + " in " + getFileName() + ". Exception while instantiating", e);
		}
		
		return null;
	}
	
	private LanguageBase loadLanguage(Class<? extends LanguageBase> type) {
		try {
			Constructor<? extends LanguageBase> constructor = type.getDeclaredConstructor();
			
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		} catch (NoSuchMethodException e) {
			log.fatal("Could not load language " + type.getName() + " in " + getFileName() + ". No zero-args constructor available.");
		} catch (SecurityException e) {
			log.fatal("Could not load language " + type.getName() + " in " + getFileName() + ". Security Error", e);
		} catch (InstantiationException e) {
			log.fatal("Could not load language " + type.getName() + " in " + getFileName() + ". Failed to instantiate", e);
		} catch (InvocationTargetException e) {
			log.fatal("Could not load language " + type.getName() + " in " + getFileName() + ". Exception while instantiating", e);
		}
		
		return null;
	}
}
