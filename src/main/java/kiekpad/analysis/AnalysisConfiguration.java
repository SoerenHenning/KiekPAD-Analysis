package kiekpad.analysis;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

import kiekpad.analysis.domain.MonitoringRecord;
import kiekpad.analysis.domain.RecordFilter;
import kiekpad.analysis.stage.RecordConverterStage;
import kiekpad.analysis.stage.RecordReconstructorStage;
import kiekpad.analysis.util.FilterStage;
import teead.AnomalyDetectionStage;
import teead.StorableAnomalyDetectionStage;
import teetime.framework.Configuration;
import teetime.stage.InstanceOfFilter;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;
import teetime.stage.io.network.TcpReaderStage;

public class AnalysisConfiguration extends Configuration {

	private final Distributor<MonitoringRecord> distributor = new Distributor<>(new CopyByReferenceStrategy());

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

	public void addAnalysis() {
		final RecordFilter recordFilter = new RecordFilter();

		// Create the stages
		final FilterStage<MonitoringRecord> filter = new FilterStage<>(recordFilter);
		final RecordConverterStage recordConverter = new RecordConverterStage();
		final AnomalyDetectionStage anomalyDetector = new StorableAnomalyDetectionStage(null, null, null, null, null);

		// Connect the stages
		super.connectPorts(this.distributor.getNewOutputPort(), filter.getInputPort());
		super.connectPorts(filter.getOutputPort(), recordConverter.getInputPort());
		super.connectPorts(recordConverter.getOutputPort(), anomalyDetector.getInputPort());
	}

}
