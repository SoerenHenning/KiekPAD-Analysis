package kiekpad.analysis.kieker;

import kiekpad.analysis.util.FilterStage;
import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;

public class RecordDistributorStage extends CompositeStage {

	private final InputPort<MonitoringRecord> inputPort;
	private final Distributor<MonitoringRecord> distributor;

	public RecordDistributorStage() {
		this.distributor = new Distributor<>(new CopyByReferenceStrategy());
		this.inputPort = this.distributor.getInputPort();
	}

	public OutputPort<MonitoringRecord> getNewOutputPort(final RecordFilter filter) {
		final FilterStage<MonitoringRecord> filterStage = new FilterStage<>(filter);

		super.connectPorts(this.distributor.getNewOutputPort(), filterStage.getInputPort());
		return filterStage.getOutputPort();
	}

	public InputPort<MonitoringRecord> getInputPort() {
		return this.inputPort;
	}

}
