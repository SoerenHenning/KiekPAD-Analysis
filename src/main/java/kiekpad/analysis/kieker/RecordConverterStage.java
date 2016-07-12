package kiekpad.analysis.kieker;

import java.time.Instant;

import teead.measurement.Measurement;
import teetime.stage.basic.AbstractTransformation;

public class RecordConverterStage extends AbstractTransformation<MonitoringRecord, Measurement> {

	@Override
	protected void execute(final MonitoringRecord record) {
		final Instant time = record.getTime();
		final double value = record.getDuration();
		final Measurement measurement = new Measurement(time, value);

		this.outputPort.send(measurement);
	}

}
