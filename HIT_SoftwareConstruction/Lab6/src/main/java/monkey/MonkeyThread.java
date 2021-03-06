package monkey;

import java.util.List;
import java.util.TimerTask;

import ladder.Ladder;
import ladder.LadderStep;
import strategy.LadderChooseStrategy;
import util.Instants;

/**
 * monkey thread class
 */
public class MonkeyThread extends TimerTask {

	private Monkey monkey;
	private List<Ladder> ladders;
	LadderChooseStrategy strategy;
	private LadderStep step;
	private LadderStep nextStep;
	private long startTime;
	private long arriveTime;
	private boolean arrived;

	private boolean nextArrived;

	/**
	 * AF : monkey represent monkey ADT containing monkey's attributes; ladders
	 * represents ladders that monkey can choose; strategy represents the
	 * strategy monkey adopts to choose ladder; step represents the concrete
	 * step on the ladder where monkey is on; nextStep represents the next
	 * ladder step monkey will move to; startTime represents the start time when
	 * monkey thread is generated; arrived shows whether the monkey has arrived.
	 * 
	 * RI : ladders is not empty. nextStep must equal or after step in monkey's
	 * direction. strategy mustn't be empty;
	 */

	/**
	 * Constructor of MonkeyThread
	 * 
	 * @param monkey
	 *            Monkey represents. non-empty
	 * @param ladders
	 *            ladders monkey can choose
	 * @param strategy
	 *            strategy the monkey uses to choose ladder
	 * @param timer
	 *            timer that controlling monkey's movement
	 */
	public MonkeyThread(Monkey monkey, List<Ladder> ladders,
			LadderChooseStrategy strategy) {
		assert monkey != null;
		assert ladders != null;

		this.monkey = monkey;
		this.ladders = ladders;
		this.strategy = strategy;
		step = null;
		nextStep = null;
		arrived = false;
		this.startTime = System.currentTimeMillis();
		this.arriveTime = -1;
	}

	/**
	 * get id of monkey
	 * 
	 * @return id of monkey
	 */
	public int getId() {
		return monkey.getId();
	}

	/**
	 * get direction of monkeys
	 * 
	 * @return direction of monkey
	 */
	public String getDirection() {
		return monkey.getDirection();
	}

	/**
	 * get velocity of monkey
	 * 
	 * @return velocity of monkey
	 */
	public int getVelocity() {
		return monkey.getVelocity();
	}

	/**
	 * check if the monkey has arrived at shore
	 * 
	 * @return return true if the monkey has arrived
	 */
	public boolean isArrived() {
		return arrived;
	}

	/**
	 * get the time costs between the time when monkeyThread is constructed and
	 * current time
	 * 
	 * @return time costs between the time when monkeyThread is constructed and
	 *         current time
	 */
	public long costTime() {
		if (arriveTime < 0)
			return Math.round(
					(System.currentTimeMillis() - this.startTime) / 1000.0);
		else
			return Math.round((this.arriveTime - this.startTime) / 1000.0);
	}

	/**
	 * get startTime of MonkeyThread
	 * 
	 * @return startTime of MonkeyThread
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * get arriveTime of MonkeyThread
	 * 
	 * @return arriveTime of MonkeyThread
	 */
	public long getArriveTime() {
		return arriveTime;
	}

	@Override
	public synchronized void run() {
		if (arrived)
			return;
		if (nextArrived) {
			// log
			System.out.printf("[��] Monkey %d arrived. [%ds]\n", getId(),
					costTime());
			step.setMonkey(null);
			arrived = true;
			// set arriveTime
			this.arriveTime = System.currentTimeMillis();
			return;
		}

		// choose a ladder if at shore
		if (null == step) {
			synchronized (ladders) {
				Ladder ladder = strategy.choose(monkey);
				if (null == ladder) {
					// log
					System.out.printf(
							"[.] Monkey %d is waiting at shore. [%ds]\n",
							getId(), costTime());
					return;
				}
				LadderStep lStart = ladder.getStepL2R(0);
				LadderStep rStart = ladder.getStepR2L(0);
				if (getDirection().equals(Instants.DIRECTION_L2R)) {
					if (lStart.getMonkey() != null)
						return;
					step = lStart;
					step.setMonkey(this.monkey);
				} else {
					if (rStart.getMonkey() != null)
						return;
					step = rStart;
					step.setMonkey(this.monkey);
				}
				// log
				System.out.printf("[o] Monkey %d choose ladder %d\n",
						monkey.getId(), ladder.getId());
			}
		}

		// update curr_step
		if (null != nextStep && nextStep != step) {
			// log
			// System.out.printf("Monkey %d move %s from %d to %d\n", getId(),
			// getDirection(), step.getIndexL2R(), nextStep.getIndexL2R());
			step.setMonkey(null);
			nextStep.setMonkey(monkey);
			step = nextStep;
		}

		if (getDirection().equals(Instants.DIRECTION_L2R))
			System.out.print("[>]");
		else
			System.out.print("[<]");
		System.out.printf(
				"Monkey %d moving to ladder %d step %d %s with velocity %d. [%ds]\n",
				getId(), step.getLadder().getId(), step.getIndexL2R(),
				getDirection(), getVelocity(), costTime());

		int remainStep = getVelocity();
		LadderStep currStep = this.step;
		while (remainStep-- > 0) {
			LadderStep nextStep = currStep.next();
			if (null == nextStep) {
				nextArrived = true;
				return;
			}
			if (nextStep.getMonkey() != null) {
				return;
			} else {
				currStep = nextStep;
				this.nextStep = nextStep;
			}
		}
	}
}
