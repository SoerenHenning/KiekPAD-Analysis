package kiekpad.analysis.stage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;

import kiekpad.analysis.domain.MonitoringRecord;
import kiekpad.analysis.domain.Trace;
import teetime.stage.basic.AbstractTransformation;

public class RecordReconstructorStage extends AbstractTransformation<IFlowRecord, MonitoringRecord> {

	private final Map<Long, Trace> traces = new HashMap<>();
	private Instant start = null;
	private long offsetInNs;

	@Override
	protected void execute(final IFlowRecord record) {
		if (this.start == null) {
			this.start = Instant.now();
			this.offsetInNs = record.getLoggingTimestamp();
		}

		if (record instanceof TraceMetadata) {
			this.handleMetadataRecord((TraceMetadata) record);
		} else if (record instanceof AbstractOperationEvent) {
			this.handleOperationEventRecord((AbstractOperationEvent) record);
		}
	}

	private void handleMetadataRecord(final TraceMetadata record) {
		// Create a new trace buffer for the new incoming trace
		final long traceID = record.getTraceId();
		final Trace trace = new Trace(record);

		this.traces.put(traceID, trace);
	}

	private void handleOperationEventRecord(final AbstractOperationEvent event) {
		// Check whether there was an incoming trace meta data record before
		if (this.traces.get(event.getTraceId()) != null) {
			if (event instanceof BeforeOperationEvent) {
				this.handleBeforeOperationEventRecord((BeforeOperationEvent) event);
			} else if (event instanceof AfterOperationEvent) {
				this.handleAfterOperationEventRecord((AfterOperationEvent) event);
			}
		} else {
			// TODO how to handle this?
		}
	}

	private void handleBeforeOperationEventRecord(final BeforeOperationEvent event) {
		this.traces.get(event.getTraceId()).pushEvent(event);
	}

	private void handleAfterOperationEventRecord(final AfterOperationEvent event) {
		final Trace trace = this.traces.get(event.getTraceId());

		final BeforeOperationEvent beforeEvent = trace.popEvent();
		if (trace.isBufferEmpty()) {
			this.traces.remove(event.getTraceId());
		}

		if (event instanceof AfterOperationFailedEvent) {
			// TODO
		}

		MonitoringRecord record = new MonitoringRecord();
		record.setOperationSignature(event.getOperationSignature());
		record.setClassSignature(event.getClassSignature());
		record.setHostname(trace.getHostname());
		record.setSessionId(trace.getSessionId());
		record.setThreadId(trace.getThreadId());
		record.setTime(this.start.plusNanos(beforeEvent.getTimestamp() - this.offsetInNs));
		record.setDuration(event.getTimestamp() - beforeEvent.getTimestamp());

		this.outputPort.send(record);

	}

}
