package kiekpad.analysis.domain;

import java.util.function.Predicate;

public class RecordFilter implements Predicate<MonitoringRecord> {

	private String operationSignature = null;
	private String classSignature = null;
	private String hostname = null;
	private String sessionId = null;
	private Long threadId = null;

	private RecordFilter() {}

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

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final RecordFilter filter = new RecordFilter();

		private Builder() {}

		public Builder operationSignature(final String operationSignature) {
			filter.operationSignature = operationSignature;
			return this;
		}

		public Builder classSignature(final String classSignature) {
			filter.classSignature = classSignature;
			return this;
		}

		public Builder hostname(final String hostname) {
			filter.hostname = hostname;
			return this;
		}

		public Builder sessionId(final String sessionId) {
			filter.sessionId = sessionId;
			return this;
		}

		public Builder threadId(final long threadId) {
			filter.threadId = threadId;
			return this;
		}

		public Builder threadId(final Long threadId) {
			filter.threadId = threadId;
			return this;
		}

		public RecordFilter build() {
			return this.filter;
		}

	}

}
