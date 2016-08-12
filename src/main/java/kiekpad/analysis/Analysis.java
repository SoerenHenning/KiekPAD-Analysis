package kiekpad.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.Duration;

import org.apache.commons.configuration2.Configuration;

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
	private final CassandraManager cassandraManager;

	public Analysis() {

		this.configuration = ConfigurationFactory.getApplicationConfiguration();
		this.analysisConfiguration = new AnalysisConfiguration();
		this.cassandraManager = new CassandraManager(this.configuration.getString("cassandra.address"), this.configuration.getInt("cassandra.port"),
				this.configuration.getString("cassandra.keyspace"), this.configuration.getInt("cassandra.timeout"));
		if (this.configuration.getBoolean("rserve.enabled")) {
			// TODO Wait for R
		}

	}

	public void addAnalysisBranchesFromPropertyFiles() {
		// TODO Names
		String directoryString = configuration.getString("branches.path");
		Path directory = Paths.get(directoryString);
		this.addAnalysisBranchesFromPropertyFiles(directory);
	}

	public void addAnalysisBranchesFromPropertyFiles(final Path directory) {
		final PathMatcher filter = directory.getFileSystem().getPathMatcher("glob:*.properties");
		try {
			Files.list(directory).filter(filter::matches).forEach(file -> addAnalysisBranchFromPropertyFile(file));
		} catch (IOException e) {
			// TODO Exception
			e.printStackTrace();
		}
	}

	public void addAnalysisBranchFromPropertyFile(final Path path) {
		// TODO var names
		String defaultConfigurationString = configuration.getString("branches.path");
		Path defaultConfiguration = Paths.get(Analysis.class.getClassLoader().getResource(defaultConfigurationString).getFile());
		Configuration configuration = ConfigurationFactory.getBranchConfiguration(path, defaultConfiguration);
		addAnalysisBranch(configuration);
	}

	public void addAnalysisBranch(final Configuration branchConfiguration) {
		// TODO Do something

		RecordFilter recordFilter = RecordFilter.builder().operationSignature("public void watchme.FooMethod.foo()").build();
		Duration slidingWindowDuration = Duration.ofHours(1);
		Duration normalizationDuration = Duration.ofSeconds(5);
		Forecaster forecaster = new RegressionForecaster();
		Aggregator aggregator = new MeanAggregator();
		CassandraAdapter storageAdapter = new CassandraAdapter(this.cassandraManager.getSession(), "measurements", "foo()-160805-2");

		this.analysisConfiguration.addAnalysis(recordFilter, slidingWindowDuration, normalizationDuration, aggregator, forecaster, storageAdapter);
	}

	public void start() {
		// This could be moved to a member variable
		final Execution<AnalysisConfiguration> analysisExecution = new Execution<>(this.analysisConfiguration);
		analysisExecution.executeBlocking();
	}

	public static void main(final String[] args) {
		Analysis analysis = new Analysis();
		// analysis.addAnalysisBranchesFromPropertyFiles(); //TODO
		analysis.start();

	}

}
