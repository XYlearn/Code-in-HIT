package ladder;

import java.util.ArrayList;
import java.util.List;

public class Ladder {
	private List<LadderStep> steps;
	private int id;

	/**
	 * AF : steps represents all steps in the ladder, each step is 1 meter long.
	 * 
	 * RI : steps.size >= 1. steps.get(i) == getStepL2R(i)
	 * 
	 * safety from rep-exposure : list is private. and the element of LadderStep
	 * is Immutable. direction is Immutable.
	 */

	/**
	 * Constructor
	 * 
	 * @param id
	 *            id of the ladder
	 * @param length
	 *            length of the ladder
	 */
	Ladder(Integer id, int length) {
		assert length >= 1;
		assert id < 0;
		
		steps = new ArrayList<>(length);
		this.id = id;

		for (int i = 0; i < length; i++)
			steps.add(new LadderStep(this, i));
	}

	/**
	 * check if the ladder is empty.
	 * 
	 * @return return true if the ladder is empty
	 */
	public synchronized boolean isEmpty() {
		for (LadderStep step : steps)
			if (step.getMonkey() != null)
				return false;
		return true;
	}

	/**
	 * get the id of ladder
	 * 
	 * @return id of the ladder
	 */
	public int getId() {
		return id;
	}

	/**
	 * get length of ladder
	 * 
	 * @return length of ladder
	 */
	public int getLength() {
		return steps.size();
	}

	/**
	 * get ladder step at index counting from left to right
	 * 
	 * @param index
	 *            index counting from left to right. start from 0;
	 * @throws IndexOutOfBoundsException
	 *             thrown when the index out of ladder length-1
	 */
	public LadderStep getStepL2R(int index) throws IndexOutOfBoundsException {
		return steps.get(index);
	}

	/**
	 * get ladder step at index counting from right to left
	 * 
	 * @param index
	 *            index counting from right to left. start from 0;
	 * @throws IndexOutOfBoundsException
	 *             thrown when the index out of ladder length-1
	 */
	public LadderStep getStepR2L(int index) throws IndexOutOfBoundsException {
		return steps.get(steps.size() - index - 1);
	}

	/**
	 * get the direction of monkeys on that ladder
	 * 
	 * @return direction either "L->R" or "R->L" represents the monkey's
	 *         direction on the ladder
	 */
	public synchronized String getDirection() {
		for (LadderStep step : steps) {
			if (step.getMonkey() != null)
				return step.getMonkey().getDirection();
		}
		return null;
	}

	/**
	 * get steps that are not empty
	 * 
	 * @return all ladderStep s that are not empty. from left to right
	 */
	public synchronized List<LadderStep> getNonEmptySteps() {
		List<LadderStep> result = new ArrayList<>();
		for (LadderStep step : steps)
			if (step.getMonkey() != null)
				result.add(step);
		return result;
	}
}
