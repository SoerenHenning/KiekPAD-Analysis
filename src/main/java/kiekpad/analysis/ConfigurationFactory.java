package kiekpad.analysis;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigurationFactory {

	private final static String DEFAULT_PROPERTY_LOCATION = "META-INF/application.properties";
	private final static String DEFAULT_BRANCH_PROPERTY_LOCATION = "META-INF/branch.properties";
	private final static String USER_PROPERTY_LOCATION = "config/application.properties";

	// BETTER A logger should be used to replace the System.out.println()

	public static Configuration getApplicationConfiguration() {
		final Configurations configurationsHelper = new Configurations();

		final CompositeConfiguration configuration = new CompositeConfiguration();

		Path path = Paths.get(USER_PROPERTY_LOCATION);
		// System.out.println("Look for user config in: " + path);
		if (Files.exists(path)) {
			// System.out.println("Config found");
			try {
				configuration.addConfiguration(configurationsHelper.properties(path.toFile()));
			} catch (ConfigurationException e) {
				throw new IllegalArgumentException("Could not load configuration from file "
						+ "\"" + USER_PROPERTY_LOCATION + "\"", e);
			}
		} else {
			// System.out.println("No config found");
		}

		try {
			configuration.addConfiguration(configurationsHelper.properties(DEFAULT_PROPERTY_LOCATION));
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException("Could not load configuration from ressource "
					+ "\"" + DEFAULT_PROPERTY_LOCATION + "\"", e);
		}

		return configuration;
	}

	public static Configuration getBranchConfiguration(final Path configFile) {
		final Configurations configurationsHelper = new Configurations();

		final CompositeConfiguration configuration = new CompositeConfiguration();

		try {
			configuration.addConfiguration(configurationsHelper.properties(configFile.toFile()));
		} catch (ConfigurationException e) {
			// TODO Exception
			throw new IllegalStateException(e);
		}

		try {
			configuration.addConfiguration(configurationsHelper.properties(DEFAULT_BRANCH_PROPERTY_LOCATION));
		} catch (ConfigurationException e) {
			// TODO Exception
			throw new IllegalStateException(e);
		}

		return configuration;
	}

}
