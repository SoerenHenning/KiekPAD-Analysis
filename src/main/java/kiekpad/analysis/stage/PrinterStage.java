package kiekpad.analysis.stage;

import kiekpad.analysis.domain.MonitoringRecord;
import teetime.framework.AbstractConsumerStage;

public class PrinterStage extends AbstractConsumerStage<MonitoringRecord> {

	@Override
	protected void execute(final MonitoringRecord element) {
		System.out.println(element.getTime() + ": " + element.getOperationSignature() + " - " + element.getDuration());
	}

}
