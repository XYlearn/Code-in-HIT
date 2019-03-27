package strategy;

import java.util.List;

import ladder.Ladder;
import ladder.LadderStep;
import monkey.Monkey;
import util.Instants;

/**
 * Chooser to choose the ladder on which the last monkey has closest distance to
 * target shore
 */
public class LastFirstLadderChooser implements LadderChooseStrategy {
	private List<Ladder> ladders;

	public LastFirstLadderChooser(List<Ladder> ladders) {
		this.ladders = ladders;
	}

	@Override
	public Ladder choose(Monkey monkey) {
		synchronized (ladders) {
			String monkeyDirection = monkey.getDirection();
			boolean l2r = monkeyDirection.equals(Instants.DIRECTION_L2R);

			// choose the ladder with fastest average speed
			int minDistance = Integer.MAX_VALUE;
			Ladder chosenLadder = null;
			for (Ladder ladder : ladders) {
				String direction = ladder.getDirection();
				// check if the ladder is empty
				if (direction == null)
					return ladder;
				// check if the ladder direction matches monkey direction
				if (!direction.equals(monkeyDirection))
					continue;

				if (l2r
						? (ladder.getStepL2R(0).getMonkey() == null)
						: (ladder.getStepR2L(0).getMonkey() == null))
					if (!direction.equals(monkeyDirection))
						continue;

				// choose the ladder last monkey on which has shortest distance
				// to shore
				List<LadderStep> nonEmptySteps = ladder.getNonEmptySteps();
				int distance;
				if (monkey.getDirection().equals(Instants.DIRECTION_L2R)) {
					distance = ladder.getLength()
							- nonEmptySteps.get(0).getIndexL2R() - 1;
				} else {
					distance = nonEmptySteps.get(nonEmptySteps.size() - 1)
							.getIndexL2R();
				}
				if (distance < minDistance) {
					minDistance = distance;
					chosenLadder = ladder;
				}
			}
			return chosenLadder;
		}
	}
}
