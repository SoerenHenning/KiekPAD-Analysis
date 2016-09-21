package kiekpad.analysis;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.Duration;

import org.apache.commons.configuration2.Configuration;

import kiekpad.analysis.domain.RecordFilter;
import teead.aggregation.Aggregator;
import teead.aggregation.AggregatorConfiguration;
import teead.aggregation.Aggregators;
import teead.forecast.AbstractRForecaster;
import teead.forecast.Forecaster;
import teead.forecast.ForecasterConfiguration;
import teead.forecast.Forecasters;
import teead.storage.CassandraAdapter;
import teetime.framework.Execution;

public class Analysis {

	private final Configuration configuration;
	private final AnalysisConfiguration analysisConfiguration;
	private final CassandraManager cassandraManager;

	// BETTER A logger should be used to replace the System.out.println()

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
		// System.out.println("Search for user config.");
		String directoryString = configuration.getString("branches.path");
		Path directory = Paths.get(directoryString);
		this.addAnalysisBranchesFromPropertyFiles(directory);
	}

	public void addAnalysisBranchesFromPropertyFiles(final Path directory) {
		// System.out.println("Use directory: " + directory);
		// System.out.println("abs: " + directory.toAbsolutePath().toString());

		final PathMatcher filter = directory.getFileSystem().getPathMatcher("glob:**.properties");

		try {
			Files.list(directory).filter(x -> filter.matches(x)).forEach(file -> addAnalysisBranchFromPropertyFile(file));
		} catch (IOException e) {
			// TODO Exception
			throw new UncheckedIOException(e);
		}
	}

	public void addAnalysisBranchFromPropertyFile(final Path path) {
		// System.out.println("Use configuration: " + path);
		Configuration config = ConfigurationFactory.getBranchConfiguration(path);
		addAnalysisBranch(config);
	}

	public void addAnalysisBranch(final Configuration branchConfig) {

		final String identifier = branchConfig.getString("id");
		if (identifier == null) {
			throw new IllegalArgumentException("The property \"id\" is required.");
		}

		// The RecordFilter builder is able to handle null values
		RecordFilter recordFilter = RecordFilter.builder()
				.operationSignature(branchConfig.getString("filter.operationSignature"))
				.classSignature(branchConfig.getString("filter.classSignature"))
				.hostname(branchConfig.getString("filter.hostname"))
				.sessionId(branchConfig.getString("filter.sessionId"))
				.threadId(branchConfig.getLong("filter.threadId", null))
				.build();

		Duration slidingWindowDuration = Duration.parse(branchConfig.getString("slidingWindowDuration"));
		Duration normalizationDuration = Duration.parse(branchConfig.getString("normalizationDuration"));

		ForecasterConfiguration forecasterConfig = new ForecasterConfiguration();
		if (this.configuration.getBoolean("rserve.enabled")) {
			forecasterConfig.put(AbstractRForecaster.HOST_CONFIGURATION_KEY, this.configuration.getString("rserve.address"));
			forecasterConfig.put(AbstractRForecaster.PORT_CONFIGURATION_KEY, this.configuration.getString("rserve.port"));
		}

		Forecaster forecaster = Forecasters.getByClassName(branchConfig.getString("forecaster"), forecasterConfig);
		Aggregator aggregator = Aggregators.getByClassName(branchConfig.getString("aggregator"), new AggregatorConfiguration());

		CassandraAdapter storageAdapter = new CassandraAdapter(this.cassandraManager.getSession(), branchConfig.getString("cassandra.table"), identifier);
		storageAdapter.setSeriesIdColumn(branchConfig.getString("cassandra.column.seriesId"));
		storageAdapter.setTimeColumn(branchConfig.getString("cassandra.column.time"));
		storageAdapter.setNanoColumn(branchConfig.getString("cassandra.column.nanos"));
		storageAdapter.setMeasurementColumn(branchConfig.getString("cassandra.column.measurement"));
		storageAdapter.setPredictionColumn(branchConfig.getString("cassandra.column.prediction"));
		storageAdapter.setAnomalyscoreColumn(branchConfig.getString("cassandra.column.anomalyscore"));

		this.analysisConfiguration.addAnalysis(recordFilter, slidingWindowDuration, normalizationDuration, aggregator, forecaster, storageAdapter);
	}

	public void start() {
		// This could be moved to a member variable in future versions of TeeTime
		final Execution<AnalysisConfiguration> analysisExecution = new Execution<>(this.analysisConfiguration);
		analysisExecution.executeBlocking();
	}

	public static void main(final String[] args) throws Exception {
		while (true) {
			Analysis analysis = new Analysis();
			analysis.addAnalysisBranchesFromPropertyFiles();
			analysis.start();
			// Restart analysis after finishing and wait for new a new sender
		}
	}

}
