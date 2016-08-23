package kiekpad.analysis.util;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class StopWatchStage extends AbstractStage {

	private final InputPort<Object> startInputPort = this.createInputPort();
	private final InputPort<Object> stopInputPort = this.createInputPort();
	private final OutputPort<Long> outputPort = this.createOutputPort();

	private long startTime;

	@Override
	protected void execute() {
		if (this.getStartInputPort().receive() != null) {
			// Override existing start time
			this.startTime = System.nanoTime();
		}
		if (this.getStopInputPort().receive() != null) {
			long endTime = System.nanoTime();
			long duration = endTime - this.startTime;
			this.getOutputPort().send(duration);
		}
	}

	public InputPort<Object> getStartInputPort() {
		return startInputPort;
	}

	public InputPort<Object> getStopInputPort() {
		return stopInputPort;
	}

	public OutputPort<Long> getOutputPort() {
		return outputPort;
	}

}
