package kiekpad.analysis;

import java.time.Duration;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

import kiekpad.analysis.domain.RecordFilter;
import kiekpad.analysis.stage.RecordConverterStage;
import kiekpad.analysis.stage.RecordDistributorStage;
import kiekpad.analysis.stage.RecordReconstructorStage;
import teead.AnomalyDetectionStage;
import teead.StorableAnomalyDetectionStage;
import teead.aggregation.Aggregator;
import teead.forecast.Forecaster;
import teead.storage.StorageAdapter;
import teetime.framework.Configuration;
import teetime.stage.InstanceOfFilter;
import teetime.stage.io.network.TcpReaderStage;

public class AnalysisConfiguration extends Configuration {

	private final RecordDistributorStage distributor = new RecordDistributorStage();

	public AnalysisConfiguration() {
		// Create the stages
		final TcpReaderStage tcpReaderStage = new TcpReaderStage();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> flowRecordFilter = new InstanceOfFilter<>(IFlowRecord.class);
		final RecordReconstructorStage recordReconstructor = new RecordReconstructorStage();

		// Connect the stages
		super.connectPorts(tcpReaderStage.getOutputPort(), flowRecordFilter.getInputPort());
		super.connectPorts(flowRecordFilter.getMatchedOutputPort(), recordReconstructor.getInputPort());
		super.connectPorts(recordReconstructor.getOutputPort(), this.distributor.getInputPort());

	}

	public void addAnalysis(final RecordFilter filter, final Duration slidingWindowDuration, final Duration normalizationDuration, final Aggregator aggregator,
			final Forecaster forecaster, final StorageAdapter storageDriver) {

		// Create the stages
		final RecordConverterStage recordConverter = new RecordConverterStage();
		final AnomalyDetectionStage anomalyDetector = new StorableAnomalyDetectionStage(slidingWindowDuration, normalizationDuration, aggregator, forecaster,
				storageDriver);

		// Connect the stages
		super.connectPorts(this.distributor.getNewOutputPort(filter), recordConverter.getInputPort());
		super.connectPorts(recordConverter.getOutputPort(), anomalyDetector.getInputPort());
	}

}
