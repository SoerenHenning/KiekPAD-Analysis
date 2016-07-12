package kiekpad.analysis.kieker;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

import teead.AnomalyDetectionStage;
import teead.StorableAnomalyDetectionStage;
import teetime.framework.Configuration;
import teetime.stage.InstanceOfFilter;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;
import teetime.stage.io.network.TcpReaderStage;

public class SimpleConfiguration extends Configuration {

	public SimpleConfiguration() {

		// Create the stages
		final TcpReaderStage tcpReaderStage = new TcpReaderStage();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> flowRecordFilter = new InstanceOfFilter<>(IFlowRecord.class);
		final RecordReconstructorStage recordReconstructor = new RecordReconstructorStage();
		final Distributor<MonitoringRecord> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final RecordConverterStage recordConverter = new RecordConverterStage();
		final AnomalyDetectionStage anomalyDetector = new StorableAnomalyDetectionStage(null, null, null, null, null);

		// Connect the stages
		super.connectPorts(tcpReaderStage.getOutputPort(), flowRecordFilter.getInputPort());
		super.connectPorts(flowRecordFilter.getMatchedOutputPort(), recordReconstructor.getInputPort());
		super.connectPorts(recordReconstructor.getOutputPort(), distributor.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), recordConverter.getInputPort());
		super.connectPorts(recordConverter.getOutputPort(), anomalyDetector.getInputPort());

	}

}
