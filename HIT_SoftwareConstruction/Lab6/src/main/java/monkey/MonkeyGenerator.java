package monkey;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import emu.Calculator;
import ladder.Ladder;
import strategy.LadderChooseStrategy;
import strategy.LastFirstLadderChooser;
import util.Instants;

/**
 * Class to generate Monkey
 */
public class MonkeyGenerator extends TimerTask {

	private static int currId = 0;

	private int maxVelocity;
	private int monkeyNumber;
	private int maxMonkeyNumber;
	private int generatedMonkeyNumber;
	private List<Ladder> ladders;
	private Timer timer;
	private LadderChooseStrategy strategy;

	private Calculator calculator = new Calculator();

	protected Random randEngine;
	protected Set<MonkeyThread> monkeyThreads;

	/**
	 * Constructor
	 * 
	 * @param timer
	 *            timer to schedule monkey threads
	 * @param maxVelocity
	 *            max velocity of monkey generated. maxVelocity >= 1
	 * @param monkeyNumber
	 *            the number of monkeys generated each time
	 * @param timerSpan
	 *            time span of generating monkeys. seconds
	 * @param ladders
	 *            ladders that can be chosen by monkeys
	 * @param strategy
	 *            strategy monkey adopts to choose a ladder. if is null,
	 *            generate random strategies
	 */
	public MonkeyGenerator(Timer timer, int maxVelocity, int monkeyNumber,
			int maxMonkeyNumber, int timeSpan, List<Ladder> ladders,
			LadderChooseStrategy strategy) {
		assert maxVelocity > 0;
		assert monkeyNumber >= 1;
		assert timer != null;
		assert ladders != null;

		this.timer = timer;
		this.maxVelocity = maxVelocity;
		this.monkeyNumber = monkeyNumber;
		this.maxMonkeyNumber = maxMonkeyNumber;
		this.generatedMonkeyNumber = 0;
		this.ladders = ladders;
		this.strategy = strategy;
		randEngine = new Random();
		monkeyThreads = new HashSet<>(maxMonkeyNumber);
	}

	/**
	 * generate a monkey
	 * 
	 * @return generate a monkey with unique ID.
	 */
	protected Monkey generateMonkey() {
		int id = generateId();
		String direction = generateRandDirection();
		int velocity = generateVelocity();
		return new Monkey(id, direction, velocity);
	}

	/**
	 * generate monkey thread
	 * 
	 * @return generated monkey thread
	 */
	protected MonkeyThread generateMonkeyThread() {
		Monkey monkey = generateMonkey();
		return new MonkeyThread(monkey, ladders, generateStrategy());
	}

	/**
	 * generate a unique id for monkey
	 * 
	 * @return unique id generated for monkey
	 */
	protected int generateId() {
		return currId++;
	}

	/**
	 * get random direction("L->R" or "R->L")
	 * 
	 * @return direction generated
	 */
	protected String generateRandDirection() {
		boolean l2r = randEngine.nextBoolean();
		if (l2r)
			return Instants.DIRECTION_L2R;
		else
			return Instants.DIRECTION_R2L;
	}

	/**
	 * generate velocity value between in range [1, maxVelocity]
	 * 
	 * @return generated velocity with value between in range [1, maxVelocity]
	 */
	protected int generateVelocity() {
		return Math.abs(randEngine.nextInt()) % maxVelocity + 1;
	}

	/**
	 * generate strategy for monkey to choose ladder
	 * 
	 * @return generated LadderChooseStrategy
	 */
	protected LadderChooseStrategy generateStrategy() {
		if (null != strategy)
			return strategy;
		return new LastFirstLadderChooser(ladders);
	}

	@Override
	public void run() {
		for (int i = 0; i < monkeyNumber; i++) {
			if (generatedMonkeyNumber >= maxMonkeyNumber)
				break;
			MonkeyThread monkeyThread = generateMonkeyThread();
			monkeyThreads.add(monkeyThread);
			++generatedMonkeyNumber;
			timer.schedule(monkeyThread, 0, 1000);
			// log
			System.out.printf(
					"[+] Generate Monkey %d move %s with velocity %d\n",
					monkeyThread.getId(), monkeyThread.getDirection(),
					monkeyThread.getVelocity());
		}
		// clean all arrived monkey threads
		Iterator<MonkeyThread> iterator = monkeyThreads.iterator();
		while (iterator.hasNext()) {
			MonkeyThread monkeyThread = iterator.next();
			if (monkeyThread.isArrived()) {
				// add to calculator
				calculator.add(monkeyThread.getStartTime(),
						monkeyThread.getArriveTime());
				// cancel thread
				monkeyThread.cancel();
				iterator.remove();
			}
		}

		if (monkeyThreads.isEmpty()) {
			// output the calculation result
			System.out.println(
					"=================================================");
			System.out.printf("Th = %f\n", calculator.calcTh());
			System.out.printf("F  = %f\n", calculator.calcF());
			timer.cancel();
		}
		timer.purge();
	}
}
