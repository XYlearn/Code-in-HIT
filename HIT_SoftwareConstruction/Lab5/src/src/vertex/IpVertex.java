package vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Node in Network. it has an individual IP address
 *
 */
public class IpVertex extends Vertex {

	/**
	 * AF : label:String represents name of the Host, ip:String represents ip
	 * address of the Host
	 * 
	 * RI : ip is in format "x.x.x.x", x is a decimal number in range [0, 255]
	 * 
	 * safety from exposure
	 */

	private String ip;

	private static final String ipRegex = "^([\\d]{1,3})\\.([\\d]{1,3})\\.([\\d]{1,3})\\.([\\d]{1,3})$";
	private static final Pattern ipPattern = Pattern.compile(ipRegex);

	protected IpVertex(String label) {
		super(label);
	}

	/**
	 * get IP of Host
	 * 
	 * @return IP of Host
	 */
	public String getIp() {
		return ip;
	}

	@Override
	protected void checkRep() {
		// super.checkRep();
		// assert validIp(this.ip);
	}

	/**
	 * Fill in IP information to Vertex.If the IP has already existed, it will
	 * be updated
	 * 
	 * @param args:
	 *            args.length >= 1, args[0] is the ip of Host
	 * @exception IllegalArgumentException
	 *                throwed when args.length < 1 or the IP is not in correct
	 *                format, and will do nothing.
	 */
	@Override
	public void fillVertexInfo(String[] args) throws IllegalArgumentException {
		if (args.length < 1)
			throw new IllegalArgumentException("Too few arguments");
		String ip = args[0];
		if (!validIp(ip))
			throw new IllegalArgumentException("Wrong IP format");
		// update ip.
		this.ip = ip;
		checkRep();
	}

	@Override
	public IpVertex clone() {
		// return wrap(this.getClass(), getLabel(), ip);
		return this;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	/**
	 * check whether a String's format is valid IP format
	 * 
	 * @param ip
	 *            String to check
	 * @return return true if the IP has correct format, else return false
	 */
	public static boolean validIp(String ip) {
		Matcher matcher = ipPattern.matcher(ip.trim());
		if (!matcher.find())
			return false;
		for (int i = 1; i <= 4; i++) {
			if (Integer.valueOf(matcher.group(i)) > 255)
				return false;
		}
		return true;
	}

	/**
	 * Generate a IpVertex Instance
	 * 
	 * @param type
	 *            Class to generate
	 * @param label
	 *            Host's name
	 * @param args
	 *            args[0] represents the ip of Host
	 * @return return null if the args is invalid
	 */
	protected static IpVertex wrap(Class<? extends IpVertex> type, String label,
			String[] args) {
		if (args.length < 1)
			return null;
		IpVertex vertex;
		try {
			vertex = type.getDeclaredConstructor(String.class)
					.newInstance(label);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			vertex.fillVertexInfo(args);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			return null;
		}
		return vertex;
	}

	/**
	 * Generate a IpVertex Instance
	 * 
	 * @param type
	 *            Class to generate
	 * @param label
	 *            Host's name
	 * @param ip
	 *            ip of Host
	 * @return return null if ip has wrong format
	 */
	protected static IpVertex wrap(Class<? extends IpVertex> type, String name,
			String ip) {
		return wrap(type, name, new String[]{ip});
	}

	@Override
	public List<String> getVertexInfo() {
		List<String> res = new ArrayList<>();
		res.add(getIp());
		return res;
	}
}
