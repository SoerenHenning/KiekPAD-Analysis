package kiekpad.analysis;

import teetime.framework.Execution;

public class Analysis {

	public static void main(final String[] args) {
		AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration();
		final Execution<AnalysisConfiguration> analysis = new Execution<>(analysisConfiguration);
		analysis.executeBlocking();
	}

}
