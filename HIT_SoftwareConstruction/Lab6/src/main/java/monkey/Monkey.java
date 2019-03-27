package monkey;

import util.Instants;

/**
 * Represent a Monkey in The emulator
 */
public class Monkey {
	private final int id;
	private final String direction;
	private final int velocity;

	/**
	 * AF : represents a Monkey in emulator. id is unique to every monkey and
	 * generated in time order(but monkey generated at the same time have
	 * different id); direction represent the monkey direction to go across the
	 * river; velocity represent the move speed of monkey
	 * 
	 * RI : id is non-negative number, direction is either "L->R" or "R->L",
	 * velocity is positive number in range[1, MV] where MV is the max-speed.
	 * 
	 * safety from rep-exposure : all fields are private and final.
	 */

	/**
	 * Constructor
	 * 
	 * @param id
	 *            unique id, non-negative
	 * @param direction
	 *            direction of monkey, either "L->R" or "R->L"
	 * @param velocity
	 *            velocity of monkey, positive integer.
	 */
	Monkey(int id, String direction, int velocity) {
		assert id >= 0;
		assert direction.equals(Instants.DIRECTION_L2R)
				|| direction.equals(Instants.DIRECTION_R2L);
		assert velocity > 0;
		
		this.id = id;
		this.direction = direction;
		this.velocity = velocity;
	}

	/**
	 * get id of Monkey
	 * 
	 * @return id of Monkey
	 */
	public int getId() {
		return id;
	}

	/**
	 * get direction of Monkey
	 * 
	 * @return direction of Monkey
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * get velocity of Monkey
	 * 
	 * @return velocity of Monkey
	 */
	public int getVelocity() {
		return velocity;
	}
}
