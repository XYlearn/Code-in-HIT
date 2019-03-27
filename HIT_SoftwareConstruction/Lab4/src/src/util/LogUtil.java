package util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class LogUtil {

	static final String LOG_ROOT = "logs/";

	public static String getLogPath(LocalDate date) {
		StringBuilder stringBuilder = new StringBuilder(LOG_ROOT);
		stringBuilder.append(date.getYear());
		stringBuilder.append("_");
		stringBuilder.append(date.getMonthValue());
		stringBuilder.append("_");
		stringBuilder.append(date.getDayOfMonth());
		stringBuilder.append(".log");
		return stringBuilder.toString();
	}

	/**
	 * get logger
	 * 
	 * @param get
	 *            logger of clazz
	 * 
	 * @return logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz.getName());
		logger.setUseParentHandlers(false);
		FileHandler fileHandler = null;
		try {
			String pathname = getLogPath(LocalDate.now());
			File file = new File(pathname);
			if (!file.exists())
				file.createNewFile();
			fileHandler = new FileHandler(pathname, false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		logger.addHandler(fileHandler);
		return logger;
	}

	public static void main(String[] args) {
		File file = new File("logs/2018_5_18.log");
		LogReader.getRecordsFromFile(file);
	}

}

class LogReader {

	private static final Pattern pattern = Pattern.compile("(?<date>\\d{4}_\\d{1,2}_\\d{1,2}\\).log");
	private static DocumentBuilder documentBuilder;

	static {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setExpandEntityReferences(false);
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			documentBuilder = factory.newDocumentBuilder();
			documentBuilder.setEntityResolver(new EntityResolver() {

				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (systemId.contains("logger.dtd"))
						return new InputSource(new StringReader(""));
					else
						return null;
				}
			});
		} catch (ParserConfigurationException e) {
			documentBuilder = null;
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * get all records that is after startTime and before endTime
	 * 
	 * @param startTime
	 *            earliest record time of filtering
	 * @param endTime
	 *            latest record time of filtering
	 * @return return all records between startTime and endTime
	 */
	public static List<LogRecord> getRecordsByTime(LocalDateTime startTime, LocalDateTime endTime) {
		List<LogRecord> records = new LinkedList<>();

		File logRoot = new File(LogUtil.LOG_ROOT);
		File[] files = logRoot.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				Matcher matcher = pattern.matcher(name);
				if (!matcher.find())
					return false;
				LocalDate date = LocalDate.parse(matcher.group("date"));
				return (startTime.toLocalDate().isBefore(date) || startTime.toLocalDate().isEqual(date))
						&& (endTime.toLocalDate().isAfter(date) || endTime.toLocalDate().isEqual(date));
			}
		});

		for (File file : files) {
			records.addAll(getRecordsFromFile(file));
		}

		records.removeIf(record -> {
			return record.dateTime.isBefore(startTime) || record.dateTime.isAfter(endTime);
		});

		return null;
	}

	/**
	 * get records filt by className
	 * 
	 * @param className
	 * @return
	 */
	public static List<LogRecord> getRecordsByClass(String className) {
		List<LogRecord> records = new LinkedList<>();

		File logRoot = new File(LogUtil.LOG_ROOT);
		File[] files = logRoot.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return true;
			}
		});
		for (File file : files) {
			records.addAll(getRecordsFromFile(file));
		}

		records.removeIf(record -> {
			return record.clazz.equals(className);
		});

		return records;
	}


	/**
	 * get records filt by methodName
	 * 
	 * @param methodName method name
	 * @return
	 */
	public static List<LogRecord> getRecordsByMethod(String methodName) {
		List<LogRecord> records = new LinkedList<>();

		File logRoot = new File(LogUtil.LOG_ROOT);
		File[] files = logRoot.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return true;
			}
		});
		for (File file : files) {
			records.addAll(getRecordsFromFile(file));
		}

		records.removeIf(record -> {
			return record.method.equals(methodName);
		});

		return records;
	}
	
	/**
	 * get records filt by Operation
	 * 
	 * @param methodName method name
	 * @return
	 */
	public static List<LogRecord> getRecordsByOperation(String operation) {
		List<LogRecord> records = new LinkedList<>();

		File logRoot = new File(LogUtil.LOG_ROOT);
		File[] files = logRoot.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return true;
			}
		});
		for (File file : files) {
			records.addAll(getRecordsFromFile(file));
		}

		records.removeIf(record -> {
			return record.operation.equals(operation);
		});

		return records;
	}
	
	/**
	 * get records filt by messageNeedle
	 * 
	 * @param messageNeedle message needle to search
	 * @return
	 */
	public static List<LogRecord> getRecordsByMessage(String messageNeedle) {
		List<LogRecord> records = new LinkedList<>();

		File logRoot = new File(LogUtil.LOG_ROOT);
		File[] files = logRoot.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return true;
			}
		});
		for (File file : files) {
			records.addAll(getRecordsFromFile(file));
		}

		records.removeIf(record -> {
			return record.message.contains(messageNeedle);
		});

		return records;
	}
	
	/**
	 * Get all logRecords from file
	 * 
	 * @param file
	 *            file to read
	 * @return return list of all log records in file
	 */
	public static List<LogRecord> getRecordsFromFile(File file) {
		List<LogRecord> records = new ArrayList<>();
		try {
			Document document = documentBuilder.parse(file);
			NodeList nodeList = document.getElementsByTagName("record");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element recordElement = (Element) nodeList.item(i);
				String dateTime = recordElement.getElementsByTagName("date").item(0).getTextContent();
				String clazz = recordElement.getElementsByTagName("class").item(0).getTextContent();
				String method = recordElement.getElementsByTagName("method").item(0).getTextContent();
				String message = recordElement.getElementsByTagName("message").item(0).getTextContent();
				LogRecord logRecord = new LogRecord();
				logRecord.dateTime = LocalDateTime.parse(dateTime);
				logRecord.clazz = clazz;
				logRecord.method = method;
				logRecord.message = message;
				records.add(logRecord);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return records;
		}

		return records;
	}

}

class LogRecord {
	public LocalDateTime dateTime;
	public String clazz;
	public String method;
	public String message;
	String operation;
}
