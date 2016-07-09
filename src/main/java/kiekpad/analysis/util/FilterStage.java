package kiekpad.analysis.util;

import java.util.function.Predicate;

import teetime.stage.basic.AbstractTransformation;

/**
 * This stage filters incoming objects and forwards only those which meet the given predicate.
 *
 * @author Nils Christian Ehmke, Sören Henning
 *
 * @param <T>
 *            The precise type of the incoming and outgoing object.
 */
public final class FilterStage<T> extends AbstractTransformation<T, T> {

	private final Predicate<T> predicate;

	public FilterStage(final Predicate<T> predicate) {
		this.predicate = predicate;
	}

	@Override
	protected void execute(final T element) {
		if (this.predicate.test(element)) {
			super.getOutputPort().send(element);
		}
	}

}
