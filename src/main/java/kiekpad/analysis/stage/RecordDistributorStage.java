package kiekpad.analysis.stage;

import kiekpad.analysis.domain.MonitoringRecord;
import kiekpad.analysis.domain.RecordFilter;
import kiekpad.analysis.util.FilterStage;
import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;

/**
 * A TeeTime stage that distributes {@link MonitoringRecord}s to output ports if they match a given {@link RecordFilter}
 *
 * @author Sören Henning
 *
 */
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
