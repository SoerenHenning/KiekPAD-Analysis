package kiekpad.analysis.domain;

import java.util.function.Predicate;

public class RecordFilter implements Predicate<MonitoringRecord> {

	private String operationSignature = null;
	private String classSignature = null;
	private String hostname = null;
	private String sessionId = null;
	private Long threadId = null;

	public RecordFilter() {}

	public void setOperationSignature(final String operationSignature) {
		this.operationSignature = operationSignature;
	}

	public void setClassSignature(final String classSignature) {
		this.classSignature = classSignature;
	}

	public void setHostname(final String hostname) {
		this.hostname = hostname;
	}

	public void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}

	public void setThreadId(final long threadId) {
		this.threadId = threadId;
	}

	@Override
	public boolean test(final MonitoringRecord record) {

		if (this.operationSignature != null && !this.operationSignature.equals(record.getOperationSignature())) {
			return false;
		}

		if (this.classSignature != null && !this.classSignature.equals(record.getClassSignature())) {
			return false;
		}

		if (this.hostname != null && !this.hostname.equals(record.getHostname())) {
			return false;
		}

		if (this.sessionId != null && !this.sessionId.equals(record.getSessionId())) {
			return false;
		}

		if (this.threadId != null && this.threadId.longValue() != record.getThreadId()) {
			return false;
		}

		return true;
	}

}
