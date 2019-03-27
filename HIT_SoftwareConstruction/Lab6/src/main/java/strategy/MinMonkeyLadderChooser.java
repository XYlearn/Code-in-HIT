package strategy;

import java.util.List;

import ladder.Ladder;
import monkey.Monkey;
import util.Instants;

/**
 * Chooser to choose the ladder has minimum monkeys
 */
public class MinMonkeyLadderChooser implements LadderChooseStrategy {

	private List<Ladder> ladders;

	/**
	 * Constructor
	 * 
	 * @param ladders
	 *            ladders can be chosen
	 */
	public MinMonkeyLadderChooser(List<Ladder> ladders) {
		this.ladders = ladders;
	}

	@Override
	public Ladder choose(Monkey monkey) {
		int minMonkeyNumber = Integer.MAX_VALUE;
		Ladder chosenLadder = null;
		synchronized (ladders) {
			String monkeyDirection = monkey.getDirection();
			boolean l2r;
			if (monkeyDirection.equals(Instants.DIRECTION_L2R))
				l2r = true;
			else
				l2r = false;
			for (Ladder ladder : ladders) {
				String direction = ladder.getDirection();
				if (direction == null)
					return ladder;

				// the first ladder step is empty
				if (l2r
						? (ladder.getStepL2R(0).getMonkey() == null)
						: (ladder.getStepR2L(0).getMonkey() == null))
					// the ladder has no monkey on it or the monkeys are all in
					// the
					// same direction
					if (direction.equals(monkeyDirection)) {
						int monkeyNumber = ladder.getNonEmptySteps().size();
						if(monkeyNumber < minMonkeyNumber) {
							minMonkeyNumber = monkeyNumber;
							chosenLadder = ladder;
						}
					}
			}
			return chosenLadder;
		}
	}

}
