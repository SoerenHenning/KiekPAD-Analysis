package kiekpad.analysis;

import java.io.File;
import java.time.Duration;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

import kiekpad.analysis.domain.MonitoringRecord;
import kiekpad.analysis.domain.RecordFilter;
import kiekpad.analysis.stage.RecordConverterStage;
import kiekpad.analysis.stage.RecordDistributorStage;
import kiekpad.analysis.stage.RecordReconstructorStage;
import kiekpad.analysis.util.ObjectFileWriterStage;
import kiekpad.analysis.util.StopWatchStage;
import teead.AnomalyDetectionStage;
import teead.StorableAnomalyDetectionStage;
import teead.aggregation.Aggregator;
import teead.forecast.Forecaster;
import teead.storage.StorageAdapter;
import teetime.framework.Configuration;
import teetime.stage.InstanceOfFilter;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;
import teetime.stage.io.network.TcpReaderStage;

public class AnalysisConfiguration extends Configuration {

	private final RecordDistributorStage distributor = new RecordDistributorStage();

	public AnalysisConfiguration() {
		// Create the stages
		final TcpReaderStage tcpReaderStage = new TcpReaderStage();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> flowRecordFilter = new InstanceOfFilter<>(IFlowRecord.class);
		final RecordReconstructorStage recordReconstructor = new RecordReconstructorStage();
		// final PrinterStage printerStage = new PrinterStage(); // TODO Temp

		// Connect the stages
		super.connectPorts(tcpReaderStage.getOutputPort(), flowRecordFilter.getInputPort());
		super.connectPorts(flowRecordFilter.getMatchedOutputPort(), recordReconstructor.getInputPort());
		super.connectPorts(recordReconstructor.getOutputPort(), this.distributor.getInputPort());
		// super.connectPorts(this.distributor.getNewOutputPort(RecordFilter.builder().build()), printerStage.getInputPort()); // TODO Temp

	}

	public void addAnalysis(final RecordFilter filter, final Duration slidingWindowDuration, final Duration normalizationDuration, final Aggregator aggregator,
			final Forecaster forecaster, final StorageAdapter storageDriver) {

		// Create the stages
		final RecordConverterStage recordConverter = new RecordConverterStage();
		final AnomalyDetectionStage anomalyDetector = new StorableAnomalyDetectionStage(slidingWindowDuration, normalizationDuration, aggregator, forecaster,
				storageDriver);

		// Create the evaluation stages
		final Distributor<MonitoringRecord> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final StopWatchStage stopWatch = new StopWatchStage();
		final ObjectFileWriterStage executionsFileWriter = new ObjectFileWriterStage(new File("executions.csv"));

		// Connect the stages
		super.connectPorts(this.distributor.getNewOutputPort(filter), distributor.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), stopWatch.getStartInputPort());
		super.connectPorts(distributor.getNewOutputPort(), recordConverter.getInputPort());
		super.connectPorts(recordConverter.getOutputPort(), anomalyDetector.getInputPort());
		super.connectPorts(anomalyDetector.getNewOutputPort(), stopWatch.getStopInputPort());
		super.connectPorts(stopWatch.getOutputPort(), executionsFileWriter.getInputPort());
	}

}
