package kiekpad.analysis.kieker;

import java.time.Instant;

import teead.measurement.Measurement;
import teetime.stage.basic.AbstractTransformation;

public class RecordConverterStage extends AbstractTransformation<MonitoringRecord, Measurement> {

	@Override
	protected void execute(final MonitoringRecord record) {
		// TODO This is wrong! record.getTimestamp() delivers nanoseconds relative to
		// an fixed arbitrary time...
		final Instant time = Instant.ofEpochMilli(record.getTimestamp());
		final double value = record.getDuration();
		final Measurement measurement = new Measurement(time, value);

		this.outputPort.send(measurement);
	}

}
