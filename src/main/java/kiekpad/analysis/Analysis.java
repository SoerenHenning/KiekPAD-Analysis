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

	public void addAnalysisBranch(final Configuration configuration) {
		// TODO Do something

		RecordFilter recordFilter = RecordFilter.builder()
				.operationSignature(configuration.getString("filter.operationSignature"))
				.classSignature(configuration.getString("filter.operationSignature"))
				.hostname(configuration.getString("filter.operationSignature"))
				.sessionId(configuration.getString("filter.sessionId"))
				.threadId(configuration.getLong("filter.threadId", null))
				.build();

		// TODO Get them from properties
		Duration slidingWindowDuration = Duration.parse(configuration.getString("slidingWindowDuration"));
		Duration normalizationDuration = Duration.parse(configuration.getString("normalizationDuration"));

		try {
			// get forecaster class by using reflection
			Class<?> algorithmClass = Class.forName(configuration.getString("forecaster"));

			// Class<?>[] constructorParameterClasses = new Class[] { TaskFarmConfiguration.class };
			// Object[] constructorParameterObjects = new Object[] { this.configuration };

			// Constructor<?> algorithmConstructor = algorithmClass.getConstructor(constructorParameterClasses);

			// algorithm = (AbstractThroughputAlgorithm) algorithmConstructor.newInstance(constructorParameterObjects); // NOPMD: returns in outer block
		} catch (Exception exception) {
			// TODO
		}

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

	public static void main(final String[] args) throws Exception {
		Analysis analysis = new Analysis();
		// analysis.addAnalysisBranchesFromPropertyFiles(); //TODO
		analysis.start();

	}

}
