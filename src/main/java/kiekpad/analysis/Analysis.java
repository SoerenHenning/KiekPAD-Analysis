package kiekpad.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.Duration;

import org.apache.commons.configuration2.Configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import kiekpad.analysis.domain.RecordFilter;
import teead.aggregation.Aggregator;
import teead.aggregation.MeanAggregator;
import teead.forecast.Forecaster;
import teead.forecast.RegressionForecaster;
import teead.storage.CassandraAdapter;
import teetime.framework.Execution;

public class Analysis {

	private final Configuration configuration;
	private final AnalysisConfiguration analysisConfiguration;

	public Analysis() {

		this.configuration = new ConfigurationProvider().createConfiguration();
		this.analysisConfiguration = new AnalysisConfiguration();

	}

	public void addAnalysisBranchesFromPropertyFiles() {
		// TODO
		Path directory = null; // TODO temp
		this.addAnalysisBranchesFromPropertyFiles(directory);
	}

	public void addAnalysisBranchesFromPropertyFiles(final Path directory) {
		final PathMatcher filter = directory.getFileSystem().getPathMatcher("glob:*.properties");
		try {
			Files.list(directory).filter(filter::matches).forEach(file -> addAnalysisBranchFromPropertyFile(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addAnalysisBranchFromPropertyFile(final Path path) {
		// TODO
		// load deafult propeties file
		// merge with this
		// call addAnalysisBranch
	}

	public void addAnalysisBranch(final Configuration branchConfiguration) {
		// TODO Do something
		String ipAddress = "192.168.99.100";
		int port = 32770;
		String keyspace = "Kiekpad";

		Cluster cluster = Cluster.builder().addContactPoint(ipAddress).withPort(port).build();
		Session session = cluster.connect(keyspace);

		RecordFilter recordFilter = RecordFilter.builder().operationSignature("public void watchme.FooMethod.foo()").build();
		Duration slidingWindowDuration = Duration.ofHours(1);
		Duration normalizationDuration = Duration.ofSeconds(5);
		Forecaster forecaster = new RegressionForecaster();
		Aggregator aggregator = new MeanAggregator();
		CassandraAdapter storageAdapter = new CassandraAdapter(session, "measurements", "foo()-160805-2");

		this.analysisConfiguration.addAnalysis(recordFilter, slidingWindowDuration, normalizationDuration, aggregator, forecaster, storageAdapter);
	}

	public void start() {
		// This could be moved to a member variable
		final Execution<AnalysisConfiguration> analysisExecution = new Execution<>(this.analysisConfiguration);
		analysisExecution.executeBlocking();
	}

	public static void main(final String[] args) {
		Analysis analysis = new Analysis();
		// analysis.addAnalysisBranchesFromPropertyFiles(); //TODo
		analysis.start();
	}

}
