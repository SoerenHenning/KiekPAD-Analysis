package kiekpad.analysis.kieker;

import java.util.ArrayDeque;
import java.util.Deque;

import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;

public class Trace {

	private final Deque<BeforeOperationEvent> buffer = new ArrayDeque<>();
	private final String hostname;
	private final String sessionId;
	private final long threadId;

	public Trace(final TraceMetadata record) {
		this.hostname = record.getHostname();
		this.sessionId = record.getSessionId();
		this.threadId = record.getThreadId();
	}

	public void pushEvent(final BeforeOperationEvent event) {
		this.buffer.push(event);
	}

	public BeforeOperationEvent popEvent() {
		return this.buffer.pop();
	}

	public boolean isEmpty() {
		return this.buffer.isEmpty();
	}

	public String getHostname() {
		return hostname;
	}

	public String getSessionId() {
		return sessionId;
	}

	public long getThreadId() {
		return threadId;
	}

}
