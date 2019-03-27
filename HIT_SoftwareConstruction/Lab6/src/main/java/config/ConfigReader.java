package config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to read config from file
 */
public class ConfigReader {

	/**
	 * read config from file
	 * 
	 * @param pathname
	 *            pathname of config file to read
	 * @return readed config. key of map represents name of argument and the
	 *         value represents the value of argument
	 * @throws IOException
	 *             throw when the pathname file can't be read or doesn't exist
	 */
	public static Map<String, Integer> readConfig(String pathname)
			throws IOException {
		Map<String, Integer> configTable = new HashMap<>();

		List<String> lines = Files.readAllLines(Paths.get(pathname));
		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty())
				continue;

			String[] parts = line.split("\\s*:\\s*");
			if (parts.length != 2)
				continue;
			String name = parts[0];
			try {
				int value = Integer.valueOf(parts[1]);
				configTable.put(name, value);
			} catch (NumberFormatException e) {
				continue;
			}
		}

		return configTable;
	}
}
