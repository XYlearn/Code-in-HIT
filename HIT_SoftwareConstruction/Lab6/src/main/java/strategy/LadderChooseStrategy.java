package strategy;

import ladder.Ladder;
import monkey.Monkey;

public interface LadderChooseStrategy {
	/**
	 * choose a ladder for monkey.
	 * 
	 * @param monkey
	 *            monkey to choose ladder for
	 * @return ladder chosen to get on. or null if no ladder chosen
	 */
	Ladder choose(Monkey monkey);
}
