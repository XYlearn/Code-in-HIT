package emu;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * class to calculate Th(Throughput rate) and F(Fairness) of emulation
 */
public class Calculator {

	private Map<Long, Long> startArriveMap = new IdentityHashMap<>();
	private int monkeyNumber = 0;
	private long startTime = Long.MAX_VALUE;
	private long arriveTime = 0;

	/**
	 * add pair of startTime and costTime to calculator
	 * 
	 * @param startTime
	 *            start time of monkeyThread
	 * @param costTime
	 *            cost time of monkeyThread
	 */
	public void add(long startTime, long arriveTime) {
		startArriveMap.put(startTime, arriveTime);
		monkeyNumber += 1;
		this.startTime = Long.min(startTime, this.startTime);
		this.arriveTime = Long.max(arriveTime, this.arriveTime);
	}

	/**
	 * calculate throughput rate of emulation
	 * 
	 * @return the result of calculation of throughput rate
	 */
	public double calcTh() {
		return (double) monkeyNumber / getCostTime(startTime, arriveTime);
	}

	/**
	 * get cost time in seconds
	 * 
	 * @param startTime
	 *            startTime
	 * @param arriveTime
	 *            endTime
	 * @return cost time in seconds
	 */
	protected long getCostTime(long startTime, long arriveTime) {
		return Math.round((arriveTime - startTime) / 1000.0);
	}

	/**
	 * calculate Fairness of emulation
	 * 
	 * @return the result of calculation of Fairness
	 */
	public double calcF() {
		List<Entry<Long, Long>> pairs = new ArrayList<>(
				startArriveMap.entrySet());

		// use simple algorithm for the scale is small
		int count = 0;
		for (int i = 0; i < pairs.size(); i++) {
			for (int j = i + 1; j < pairs.size(); j++) {
				long startTime1 = pairs.get(i).getKey();
				long startTime2 = pairs.get(j).getKey();
				long arriveTime1 = pairs.get(i).getValue();
				long arriveTime2 = pairs.get(j).getValue();
				if (getCostTime(startTime1, startTime2)
						* getCostTime(arriveTime1, arriveTime2) >= 0)
					count += 1;
				else
					count -= 1;
			}
		}
		return (double) count / combination(monkeyNumber, 2);
	}

	/**
	 * calculate C_m^n
	 * 
	 * @param m
	 *            total number of combination
	 * @param n
	 *            number to combine
	 * @return combination number result
	 */
	protected long combination(int m, int n) {
		long res = 1;
		for (int i = m - n + 1; i <= m; i++)
			res *= i;
		return res / fact(n);
	}

	protected long fact(int n) {
		long res = 1;
		for (int i = 1; i <= n; i++)
			res *= i;
		return res;
	}
}
