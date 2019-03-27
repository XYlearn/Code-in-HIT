package vertex;

/**
 * Represents WirelessRouter in NetworkTopolopy
 */
public class WirelessRouter extends Router {

	/**
	 * Constructor
	 * 
	 * @param label label of WirelessRouter
	 */
	protected WirelessRouter(String label) {
		super(label);
	}

	@Override
	public WirelessRouter clone() {
		return wrap(label, new String[] { getIp() });
	}

	/**
	 * Generate WirelessRouter instance
	 * 
	 * @param label
	 *            label of vertex
	 * @param args
	 *            arguments of vertex
	 * @return generated Instance
	 */
	public static WirelessRouter wrap(String label, String[] args) {
		return (WirelessRouter) IpVertex.wrap(WirelessRouter.class, label, args);
	}

}
