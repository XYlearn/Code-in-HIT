package ladder;

import monkey.Monkey;
import util.Instants;

/**
 * represents a one-meter-long ladder step on ladder
 */
public class LadderStep {
	private Monkey monkey;
	private final int indexL2R;
	private final Ladder ladder;

	/**
	 * AF : monkey represents Monkey on the ladder. indexL2R represents the
	 * order of the ladder step; ladder represents ladder this step belongs to
	 * 
	 * RI : indexL2R >= 0 and < ladder.length. ladder is non-empty
	 * 
	 * safty from rep-exposure : monkey is Immutable. indexL2R is immutable,
	 * 
	 * note : ladder should be syncronized when accessed
	 */

	/**
	 * Constructor
	 * 
	 * @param ladder
	 *            ladder the ladder step belongs to
	 * @param indexL2R
	 *            index of the step counting from left to right
	 */
	public LadderStep(Ladder ladder, int indexL2R) {
		assert ladder != null;
		assert indexL2R >= 0;
		assert indexL2R < ladder.getLength();

		this.monkey = null;
		this.ladder = ladder;
		this.indexL2R = indexL2R;
	}

	/**
	 * set Monkey on the step
	 * 
	 * @param monkey
	 *            monkey on the ladder steps
	 */
	public synchronized void setMonkey(Monkey monkey) {
		this.monkey = monkey;
	}

	/**
	 * get Monkey on the step
	 * 
	 * @return Monkey on the step
	 */
	public synchronized Monkey getMonkey() {
		return monkey;
	}

	/**
	 * get the ladder this ladder step is on
	 * 
	 * @return the ladder this ladder step is on
	 */
	public Ladder getLadder() {
		return ladder;
	}

	/**
	 * get index of the step counting from left to right
	 * 
	 * @return index of the step counting from left to right
	 */
	public int getIndexL2R() {
		return indexL2R;
	}

	/**
	 * get next step the monkey can choose. this.step shouldn't be null
	 * 
	 * @return next step the monkey can chooses. if the next step is out of
	 *         ladder, return null
	 */
	public LadderStep next() {
		try {
			if (ladder.getDirection().equals(Instants.DIRECTION_L2R))
				return ladder.getStepL2R(this.getIndexL2R() + 1);
			else
				return ladder.getStepL2R(this.getIndexL2R() - 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
