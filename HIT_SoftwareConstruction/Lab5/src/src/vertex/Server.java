package vertex;

/**
 * represents a Server in Network Topology
 */
public class Server extends IpVertex {
	
	/**
	 * AF, RI and safty from rep exposure : see IpVertex
	 */

	/**
	 * Constructor set label field
	 * 
	 * @param label
	 *            label of Vertex
	 */
	protected Server(String label) {
		super(label);
	}

	/**
	 * Generate a Server Instance
	 * 
	 * @param label
	 *            Host's name
	 * @param args
	 *            args[0] represents the ip of Host
	 * @return return null if the args is invalid
	 */
	public static Server wrap(String label, String[] args) {
		IpVertex vertex = IpVertex.wrap(Server.class, label, args);
		if (null == vertex)
			return null;
		if (vertex instanceof Server)
			return (Server) vertex;
		else
			throw new RuntimeException("Internal Error!");
	}

	/**
	 * Generate a Server Instance
	 * 
	 * @param label
	 *            Host's name
	 * @param ip
	 *            IP of Host
	 * @return return null if the args is invalid
	 */
	public static Server wrap(String label, String ip) {
		return wrap(label, new String[] { ip });
	}

}
