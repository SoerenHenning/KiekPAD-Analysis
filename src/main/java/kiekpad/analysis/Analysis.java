package kiekpad.analysis;

import java.io.File;
import java.net.URL;
import java.time.Duration;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import kiekpad.analysis.domain.RecordFilter;
import teead.aggregation.MeanAggregator;
import teead.forecast.RegressionForecaster;
import teead.storage.CassandraAdapter;
import teetime.framework.Execution;

public class Analysis {

	public static void main(final String[] args) {

		Configurations configs = new Configurations();

		ClassLoader cl = Analysis.class.getClassLoader();
		URL url = cl.getResource("META-INF/application.properties");
		File file = new File(url.getFile());

		CompositeConfiguration cc = new CompositeConfiguration();

		try {
			Configuration config = configs.properties(file);
			cc.addConfiguration(config);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(cc.getString("name"));

		executeAnalysis(buildExampleConfiguration());
	}

	public static void executeAnalysis(final AnalysisConfiguration analysisConfiguration) {
		final Execution<AnalysisConfiguration> analysis = new Execution<>(analysisConfiguration);
		analysis.executeBlocking();
	}

	private static AnalysisConfiguration buildExampleConfiguration() {
		String ipAddress = "192.168.99.100";
		int port = 32770;
		String keyspace = "Kiekpad";

		Cluster cluster = Cluster.builder().addContactPoint(ipAddress).withPort(port).build();
		Session session = cluster.connect(keyspace);

		AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration();
		RecordFilter recordFilter = RecordFilter.builder().operationSignature("public void watchme.FooMethod.foo()").build();
		analysisConfiguration.addAnalysis(recordFilter, Duration.ofHours(1), Duration.ofSeconds(5), new MeanAggregator(), new RegressionForecaster(),
				new CassandraAdapter(session, "measurements", "foo()-160805-2"));

		return analysisConfiguration;
	}

}
