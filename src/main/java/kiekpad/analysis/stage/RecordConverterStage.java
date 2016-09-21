package kiekpad.analysis.stage;

import java.time.Instant;

import kiekpad.analysis.domain.MonitoringRecord;
import teead.measurement.Measurement;
import teetime.stage.basic.AbstractTransformation;

/**
 * A TeeTime stage that transforms {@link MonitoringRecord}s to {@link Measurement}s
 *
 * @author Sören Henning
 *
 */
public class RecordConverterStage extends AbstractTransformation<MonitoringRecord, Measurement> {

	@Override
	protected void execute(final MonitoringRecord record) {
		final Instant time = record.getTime();
		final double value = record.getDuration();
		final Measurement measurement = new Measurement(time, value);

		this.outputPort.send(measurement);
	}

}
