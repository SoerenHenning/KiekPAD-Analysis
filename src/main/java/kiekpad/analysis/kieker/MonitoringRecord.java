package kiekpad.analysis.kieker;

public class MonitoringRecord {

	private String operationSignature;
	private String classSignature;
	private String hostname;
	private String sessionId;
	private long threadId;
	// Java 8 Instant could be more useful
	private long timestamp; // timestamp in nanoseconds returned by System.nanoTime()
	// Java 8 Duration could be more useful
	private long duration; // in nanoseconds

	public MonitoringRecord() {}

	public MonitoringRecord(final String operationSignature, final String classSignature, final String hostname, final String sessionId, final long threadId,
			final long timestamp, final long duration) {
		this.operationSignature = operationSignature;
		this.classSignature = classSignature;
		this.hostname = hostname;
		this.sessionId = sessionId;
		this.threadId = threadId;
		this.timestamp = timestamp;
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

}
