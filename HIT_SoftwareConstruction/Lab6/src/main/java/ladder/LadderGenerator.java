package ladder;

/**
 * class to generate ladder with unique ladder ID;
 */
public class LadderGenerator {
	private static int ladderID = 0;

	/**
	 * generate ladder with unique id
	 * 
	 * @param length
	 *            length of ladder to generate
	 * @return ladder with unique id an length given
	 */
	public Ladder generateLadder(int length) {
		return new Ladder(ladderID++, length);
	}
}
