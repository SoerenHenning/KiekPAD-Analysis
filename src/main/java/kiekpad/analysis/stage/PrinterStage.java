package kiekpad.analysis.stage;

import kiekpad.analysis.domain.MonitoringRecord;
import teetime.framework.AbstractConsumerStage;

/**
 * A stage that prints the data of {@code MonitoringRecord}s to System.out
 *
 * @author Sören Henning
 *
 */
public class PrinterStage extends AbstractConsumerStage<MonitoringRecord> {

	@Override
	protected void execute(final MonitoringRecord element) {
		System.out.println(element.getTime() + ": " + element.getOperationSignature() + " - " + element.getDuration());
	}

}
