package kiekpad.analysis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;

import teetime.framework.AbstractConsumerStage;

public class CountCSVWriterStage extends AbstractConsumerStage<Object> {

	private final PrintWriter printWriter;
	private int counter = 0;
	private final Instant start;

	public CountCSVWriterStage(final File file) {
		try {
			this.printWriter = new PrintWriter(file);
		} catch (FileNotFoundException exception) {
			throw new IllegalStateException(exception);
		}
		this.start = Instant.now();
		writeRow();
	}

	@Override
	protected void execute(final Object object) {
		this.counter++;
		writeRow();
	}

	@Override
	public void onTerminating() {
		this.printWriter.close();
	}

	private void writeRow() {
		final long time = Duration.between(this.start, Instant.now()).toNanos();
		this.printWriter.write(time + "," + this.counter);
	}

}
