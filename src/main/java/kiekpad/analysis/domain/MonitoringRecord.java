package kiekpad.analysis.domain;

import java.time.Instant;

/**
 * This class represents a monitored operation call.
 *
 * @author Sören Henning
 *
 */
public class MonitoringRecord {

	private String operationSignature;
	private String classSignature;
	private String hostname;
	private String sessionId;
	private long threadId;
	private Instant time;
	private long duration; // in nanoseconds

	public MonitoringRecord() {}

	public MonitoringRecord(final String operationSignature, final String classSignature, final String hostname, final String sessionId, final long threadId,
			final Instant time, final long duration) {
		this.operationSignature = operationSignature;
		this.classSignature = classSignature;
		this.hostname = hostname;
		this.sessionId = sessionId;
		this.threadId = threadId;
		this.time = time;
		this.duration = duration;
	}

	public String getOperationSignature() {
		return operationSignature;
	}

	public void setOperationSignature(final String operationSignature) {
		this.operationSignature = operationSignature;
	}

	public String getClassSignature() {
		return classSignature;
	}

	public void setClassSignature(final String classSignature) {
		this.classSignature = classSignature;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(final String hostname) {
		this.hostname = hostname;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(final long threadId) {
		this.threadId = threadId;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(final Instant time) {
		this.time = time;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

}
