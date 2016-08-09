package kiekpad.analysis;

import java.io.File;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigurationProvider {

	private final static String DEFAULT_PROPERTY_LOCATION = "META-INF/application.properties";

	public Configuration createConfiguration() {
		final Configurations configurationsHelper = new Configurations();

		final CompositeConfiguration configuration = new CompositeConfiguration();

		try {
			File file = new File(this.getClass().getClassLoader().getResource(DEFAULT_PROPERTY_LOCATION).getFile());
			configuration.addConfiguration(configurationsHelper.properties(file));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			File file = new File(this.getClass().getClassLoader().getResource("META-INF/application.properties").getFile());
			Configuration defaultConfig = configurationsHelper.properties(file);
			configuration.addConfiguration(defaultConfig);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return configuration;
	}

}
