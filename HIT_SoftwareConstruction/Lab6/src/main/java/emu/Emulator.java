package emu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import config.ConfigReader;
import ladder.Ladder;
import ladder.LadderGenerator;
import monkey.MonkeyGenerator;
import strategy.FirstPossibleLadderChooser;
import strategy.LastFirstLadderChooser;
import strategy.MinMonkeyLadderChooser;

public class Emulator {

	public static void main(String[] args) {
		Emulator emulator = null;
		try {
			emulator = constructFromFile("config.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Fail to load config file.");
			System.exit(0);
		}
		emulator.emulate();
	}

	/**
	 * Construct an Emulator from config file
	 * 
	 * @return generated Emulator from config file
	 */
	public static Emulator constructFromFile(String pathname)
			throws IOException {
		Map<String, Integer> config = ConfigReader.readConfig(pathname);
		Integer ladderNumber = config.get("LadderNumber");
		Integer ladderLength = config.get("LadderLength");
		Integer timeSpan = config.get("TimeSpan");
		Integer monkeyNumber = config.get("MonkeyNumber");
		Integer maxMonkeyNumber = config.get("MaxMonkeyNumber");
		Integer maxVelocity = config.get("MaxVelocity");
		if (null == ladderNumber || null == ladderLength || null == timeSpan
				|| null == monkeyNumber || null == maxMonkeyNumber
				|| null == maxVelocity)
			return null;
		else
			return new Emulator(ladderNumber, ladderLength, timeSpan,
					monkeyNumber, maxMonkeyNumber, maxVelocity);
	}

	private int ladderNumber;
	private int ladderLength;
	private int timeSpan;
	private int monkeyNumber;
	private int maxMonkeyNumber;
	private int maxVelocity;
	private Timer timer;
	private List<Ladder> ladders;
	private MonkeyGenerator monkeyGenerator;

	/**
	 * Constructor
	 * 
	 * @param ladderNumber
	 *            ladder number
	 * @param ladderLength
	 *            length of each ladder
	 * @param timeSpan
	 *            time span of generating monkeys
	 * @param monkeyNumber
	 *            the number of monkeys generated each time
	 * @param maxMonkeyNumber
	 *            max number of monkeys
	 * @param maxVelocity
	 *            max velocity of monkeys
	 */
	public Emulator(int ladderNumber, int ladderLength, int timeSpan,
			int monkeyNumber, int maxMonkeyNumber, int maxVelocity) {
		this.ladderNumber = ladderNumber;
		this.ladderLength = ladderLength;
		this.timeSpan = timeSpan;
		this.monkeyNumber = monkeyNumber;
		this.maxMonkeyNumber = maxMonkeyNumber;
		this.maxVelocity = maxVelocity;
		this.intialize();

	}

	/**
	 * initialize all components according to given attributes
	 */
	protected void intialize() {
		this.timer = new Timer();
		LadderGenerator ladderGenerator = new LadderGenerator();

		this.ladders = new ArrayList<>(ladderNumber);
		for (int i = 0; i < ladderNumber; i++)
			ladders.add(ladderGenerator.generateLadder(ladderLength));

		this.monkeyGenerator = new MonkeyGenerator(timer, maxVelocity,
				monkeyNumber, maxMonkeyNumber, timeSpan, ladders,
				new MinMonkeyLadderChooser(ladders));
	}

	/**
	 * start to emulate
	 */
	public void emulate() {
		System.out.println("Start to emulate");
		System.out.printf(
				"LadderNumber\t: \t%d\nLadderLength\t: \t%d\nTimeSpan\t: \t%d\nMonkeyNumber\t: \t%d\nMaxMonkeyNumber\t: \t%d\nMaxVelocity\t: \t%d\n",
				ladderNumber, ladderLength, timeSpan, monkeyNumber,
				maxMonkeyNumber, maxVelocity);
		System.out.println("=================================================");
		timer.schedule(monkeyGenerator, 1000, timeSpan * 1000);
	}
}
