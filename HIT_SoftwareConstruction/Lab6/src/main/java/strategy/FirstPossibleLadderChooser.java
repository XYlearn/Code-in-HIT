package strategy;

import java.util.List;

import ladder.Ladder;
import monkey.Monkey;
import util.Instants;

/**
 * chooser to choose the first possible ladder that has no monkey on it or the
 * monkey's direction is the same to direction of all monkeys on the ladder. if
 * no such ladder, the wait on the shore
 */
public class FirstPossibleLadderChooser implements LadderChooseStrategy {

	private List<Ladder> ladders;

	public FirstPossibleLadderChooser(List<Ladder> ladders) {
		this.ladders = ladders;
	}

	@Override
	public Ladder choose(Monkey monkey) {
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
					// the monkeys are all in the same direction
					if (direction.equals(monkeyDirection))
						return ladder;
			}
			return null;
		}
	}

}
