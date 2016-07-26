package kiekpad.analysis;

import java.time.Duration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import kiekpad.analysis.domain.RecordFilter;
import teead.aggregation.MeanAggregator;
import teead.forecast.RegressionForecaster;
import teead.storage.CassandraDriver;
import teetime.framework.Execution;

public class Analysis {

	public static void main(final String[] args) {
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
		analysisConfiguration.addAnalysis(new RecordFilter(), Duration.ofHours(1), Duration.ofSeconds(5), new MeanAggregator(), new RegressionForecaster(),
				new CassandraDriver(session, "measurements", "example-name"));

		return analysisConfiguration;
	}

}
