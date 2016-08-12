package kiekpad.analysis;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigurationFactory {

	private final static String DEFAULT_PROPERTY_LOCATION = "META-INF/application.properties";
	private final static String USER_PROPERTY_LOCATION = "application.properties";

	public static Configuration getApplicationConfiguration() {
		final Configurations configurationsHelper = new Configurations();

		final CompositeConfiguration configuration = new CompositeConfiguration();

		Path path = Paths.get(USER_PROPERTY_LOCATION);
		if (Files.exists(path)) {
			try {
				configuration.addConfiguration(configurationsHelper.properties(path.toFile()));
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File file = new File(Analysis.class.getClassLoader().getResource(DEFAULT_PROPERTY_LOCATION).getFile());
		try {
			configuration.addConfiguration(configurationsHelper.properties(file));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return configuration;
	}

	public static Configuration getBranchConfiguration(final Path configFile, final Path defaultConfigFile) {
		final Configurations configurationsHelper = new Configurations();

		final CompositeConfiguration configuration = new CompositeConfiguration();

		try {
			configuration.addConfiguration(configurationsHelper.properties(configFile.toFile()));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			configuration.addConfiguration(configurationsHelper.properties(defaultConfigFile.toFile()));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return configuration;
	}

}
