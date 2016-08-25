package kiekpad.analysis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import teetime.framework.AbstractConsumerStage;

public class ObjectFileWriterStage extends AbstractConsumerStage<Object> {

	private final PrintWriter printWriter;

	public ObjectFileWriterStage(final File file) {
		try {
			this.printWriter = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void execute(final Object object) {
		this.printWriter.println(object.toString());
		this.printWriter.flush();
	}

	@Override
	public void onTerminating() throws Exception {
		this.printWriter.close();
		super.onTerminating();
	}

}
