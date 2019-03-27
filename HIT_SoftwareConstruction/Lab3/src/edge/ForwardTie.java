package edge;

import java.util.List;

import vertex.Vertex;

/**
 * Represents CommentTie in SocialNetwork. It is an weighted directed edge with
 * no loop that means edge target shouldn't be equal to edge source. And the
 * weight should in range (0, 1].
 */
public class ForwardTie extends Tie {

	/**
	 * AF, RI, safety from rep exposure : see Tie
	 */
	
	/**
	 * ForwardTie Constructor
	 * 
	 * @param label
	 *            label of edge
	 * @param weight
	 *            weight of edge
	 */
	protected ForwardTie(String label, double weight) {
		super(label, weight);
	}

	/**
	 * generate ForwardTie Instance
	 * 
	 * @param label
	 *            edge's label
	 * @param vertices
	 *            vertices of the edge, the first is source and the second is target
	 * @param weight
	 *            weight of the Tie
	 * @return return new instance if generate success; if the arguments doesn't
	 *         match {@link FriendTie} description or the type is not Found, function will
	 *         fail and return null.
	 */
	public static ForwardTie wrap(String label, List<Vertex> vertices, double weight) {
		try {
			return (ForwardTie) Tie.wrap(ForwardTie.class, label, vertices, weight);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
