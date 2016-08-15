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
	private final static String USER_PROPERTY_LOCATION = "application.properties";

	public static Configuration getApplicationConfiguration() {
		final Configurations configurationsHelper = new Configurations();

		final CompositeConfiguration configuration = new CompositeConfiguration();

		Path path = Paths.get(USER_PROPERTY_LOCATION);
		if (Files.exists(path)) {
			try {
				configuration.addConfiguration(configurationsHelper.properties(path.toFile()));
			} catch (ConfigurationException e) {
				throw new IllegalArgumentException("Could not load configuration from file "
						+ "\"" + USER_PROPERTY_LOCATION + "\"", e);
			}
		}

		// File file = new File(Analysis.class.getClassLoader().getResource(DEFAULT_PROPERTY_LOCATION).getFile());

		// URL url = Analysis.class.getClassLoader().getResource("");
		// Path path12 = Paths.get(url.toString());
		// System.out.println(path12); // TODO
		try {
			// System.out.println("file: " + file); // TODO
			// System.out.println("file.exists()" + file.exists()); // TODO
			// System.out.println("file.toPath()" + file.toPath()); // TODO
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			configuration.addConfiguration(configurationsHelper.properties(DEFAULT_BRANCH_PROPERTY_LOCATION));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return configuration;
	}

}
