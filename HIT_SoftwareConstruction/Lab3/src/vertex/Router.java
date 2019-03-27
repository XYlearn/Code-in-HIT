package vertex;

public class Router extends IpVertex {
	
	/**
	 * AF, RI and safty from rep exposure : see IpVertex
	 */

	/**
	 * Constructor set label field
	 * 
	 * @param label
	 *            label of Vertex
	 */
	protected Router(String label) {
		super(label);
	}

	/**
	 * Generate a Router Instance
	 * 
	 * @param label
	 *            Host's name
	 * @param args
	 *            args[0] represents the ip of Host
	 * @return return null if the args is invalid
	 */
	public static Router wrap(String label, String[] args) {
		IpVertex vertex = IpVertex.wrap(Router.class, label, args);
		if (null == vertex)
			return null;
		if (vertex instanceof Router)
			return (Router) vertex;
		else
			throw new RuntimeException("Internal Error!");
	}

	/**
	 * Generate a Router Instance
	 * 
	 * @param label
	 *            Host's name
	 * @param ip
	 *            IP of Host
	 * @return return null if the args is invalid
	 */
	public static Router wrap(String label, String ip) {
		return wrap(label, new String[] { ip });
	}

}
