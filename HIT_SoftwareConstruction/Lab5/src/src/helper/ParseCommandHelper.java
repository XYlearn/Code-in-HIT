package helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.*;

import edge.Edge;
import exception.CommandException;
import graph.Graph;
import graph.SocialNetwork;
import helper.reader.FileReadStrategy;
import helper.reader.ReaderFileReader;
import util.LogUtil;
import graph.ConcreteGraph;
import vertex.Vertex;

/**
 * Class to parse command from string content or file and parse and execute
 * command lines.
 */
public class ParseCommandHelper {

	/**
	 * AF: use Set<Class<?>> to save edgeTypes; use Class<?> to save graphType;
	 * use Set<Class<?>> to represents vertexTypes; use String to store
	 * graphName; use Set<Edge> to represent edges; use Map<Vertex> to represent
	 * vertices. All these were parsed from content
	 * 
	 * RI: element in vertices has class in vertexTypes. element in edges has
	 * class in edgeTypes. vertices in edge must in vertices
	 * 
	 * safety from rep exposure: only the generated graph can be accessed out of
	 * the class.
	 */

	public static FileReadStrategy ioStrategy;

	protected final static Pattern GTRegex; // Graph Type regular expression
	protected final static Pattern GNRegex; // Graph Name regular expression
	protected final static Pattern ETRegex; // Edge Type regular expression
	protected final static Pattern EBRegex; // Edge Build regular expression
	protected final static Pattern HEBRegex; // Hyper Edge Build regular
												// expression
	protected final static Pattern VTRegex; // Vertex Type regular expression
	protected final static Pattern VBRegex; // Vertex Build regular expression

	protected final static Pattern VARegex; // vertex add regular expression
	protected final static Pattern VDRegex; // vertex delete regular expression
	protected final static Pattern EARegex; // edge add regular expression
	protected final static Pattern EDRegex; // edge delete regular expression
	protected final static Pattern HEARegex; // hyperedge add regular expression

	protected static final Logger logger = null;//LogUtil.getLogger(ParseCommandHelper.class);

	/**
	 * build regex
	 */
	static {
		// Graph Type regular expression
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("^GraphType[\\s]*=[\\s]*");
		stringBuilder.append("(?<type>[\\w]+)$");
		GTRegex = Pattern.compile(stringBuilder.toString());

		// Graph Name regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^GraphName[\\s]*=[\\s]*");
		stringBuilder.append("(?<name>[\\w]+)$");
		GNRegex = Pattern.compile(stringBuilder.toString());

		// Edge Type regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^EdgeType[\\s]*=[\\s]*");
		stringBuilder.append("(?<types>([\\w]+[\\s]*,[\\s]*)*[\\w]+)$");
		ETRegex = Pattern.compile(stringBuilder.toString());

		// Edge Build regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^Edge[\\s]*=[\\s]*");
		stringBuilder.append("<[\\s]*(?<label>[\\w]+)[\\s]*,"); // label
		stringBuilder.append("[\\s]*(?<type>[\\w]+)[\\s]*,"); // type
		stringBuilder.append("[\\s]*(?<weight>-?[\\d]+(\\.[\\d]+)?)[\\s]*,"); // weight
		stringBuilder.append("[\\s]*(?<startLabel>[\\w]+)[\\s]*,"); // startVertex
		stringBuilder.append("[\\s]*(?<endLabel>[\\w]+)[\\s]*,"); // endVertex
		stringBuilder.append("[\\s]*(?<weighted>(Yes|No))[\\s]*>$"); // if it is
																		// weighted
		EBRegex = Pattern.compile(stringBuilder.toString());

		// Hyper Edge Build regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^HyperEdge[\\s]*=[\\s]*");
		stringBuilder.append("<[\\s]*(?<label>[\\w]+)[\\s]*,"); // label
		stringBuilder.append("[\\s]*(?<type>[\\w]+)[\\s]*,"); // type
		stringBuilder.append(
				"[\\s]*\\{[\\s]*(?<vertices>(([\\w]+)[\\s]*,[\\s]*)*([\\w]+))"); // vertices
		stringBuilder.append("[\\s]*\\}>$");
		HEBRegex = Pattern.compile(stringBuilder.toString());

		// Vertex Type regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^VertexType[\\s]*=[\\s]*");
		stringBuilder.append("(?<types>(([\\w]+)[\\s]*,[\\s]*)*([\\w]+))$"); // types
		VTRegex = Pattern.compile(stringBuilder.toString());

		// Vertex Build regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^Vertex[\\s]*=[\\s]*");
		stringBuilder.append("<[\\s]*(?<label>[\\w]+)[\\s]*,"); // label
		stringBuilder.append("[\\s]*(?<type>[\\w]+)[\\s]*,"); // type
		stringBuilder.append(
				"[\\s]*<((?<args>[\\s]*(.+?[\\s]*,[\\s]*)*.+?[\\s]*)?>[\\s]*)>$"); // args
		VBRegex = Pattern.compile(stringBuilder.toString());

		// vertex add regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^\\s*vertex\\s+--add\\s+");
		stringBuilder.append(
				"(?<label>\\w+)\\s+(?<type>\\w+)(?<args>(\\s+\\w+)*)\\s*$");
		VARegex = Pattern.compile(stringBuilder.toString());

		// vertex delete regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^\\s*vertex\\s+--delete\\s+");
		stringBuilder.append("\"(?<regex>.+?)\"\\s*$");
		VDRegex = Pattern.compile(stringBuilder.toString());

		// edge add regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^\\s*edge\\s+--add\\s+");
		stringBuilder.append(
				"(?<label>\\w+)\\s+(?<type>\\w+)(\\s+(?<weight>-?[\\d]+(\\.[\\d]+)?))?\\s+");
		stringBuilder.append("(?<v1>\\w+)\\s*,\\s*(?<v2>\\w+)\\s*$");
		EARegex = Pattern.compile(stringBuilder.toString());

		// edge delete regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^\\s*edge\\s+--delete\\s+");
		stringBuilder.append("\"(?<regex>.+?)\"\\s*$");
		EDRegex = Pattern.compile(stringBuilder.toString());

		// hyperedge add regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^\\s*hyperedge\\s+--add\\s+");
		stringBuilder.append(
				"(?<label>\\w+)\\s+(?<type>\\w+)\\s+(?<vertices>(\\w+\\s*,\\s*)*(\\w+))\\s*$");
		HEARegex = Pattern.compile(stringBuilder.toString());
	}

	private final Set<Class<?>> edgeTypes;
	private Class<?> graphType;
	private final Set<Class<?>> vertexTypes;
	private String graphName;
	private final Set<Edge> edges;
	private final Map<String, Vertex> vertices;
	private ConcreteGraph graph;

	protected static FileReadStrategy reader = new ReaderFileReader();

	/**
	 * constructor of ParseCommandHelper
	 */
	public ParseCommandHelper() {
		edgeTypes = new HashSet<>();
		vertexTypes = new HashSet<>();
		edges = new HashSet<>();
		vertices = new HashMap<>();
		graphName = "";
	}

	/**
	 * set reader of ParseCommandHelper
	 * 
	 * @param reader
	 *            reader to set
	 * @return return true if set success
	 */
	public static boolean setReader(FileReadStrategy reader) {
		if (null == reader)
			return false;
		else {
			ParseCommandHelper.reader = reader;
			return true;
		}
	}

	/**
	 * get Graph built last time. notice that the graph is modifiable.
	 * 
	 * @return the last built graph. if no graph built, return null
	 */
	public ConcreteGraph getGraph() {
		return this.graph;
	}

	/**
	 * parse GraphType Command.The first GraphType command will be adopted. The
	 * command must be in format:
	 * <p>
	 * GraphType = ${TYPENAME}
	 * </p>
	 * 
	 * @param commandLine
	 *            trimmed command line text
	 * @return return valid graph class
	 * @throws CommandException
	 *             raise when Graph type is Not Found
	 */
	protected Class<?> parseGraphType(String commandLine)
			throws CommandException {

		Matcher matcher = GTRegex.matcher(commandLine);

		if (!matcher.find())
			return null;
		try {
			graphType = Class.forName("graph." + matcher.group("type"));
			return graphType;
		} catch (ClassNotFoundException e) {
			String logText = "Graph Type \"" + matcher.group("type")
					+ "\" Not Found";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(matcher.start("type"));
			// logger.info(exception.getLogText());
			throw exception;
		}
	}

	/**
	 * parse GraphName Command.The first command of this type will be adopted
	 * 
	 * @param commandLine
	 *            trimmed command line text
	 * @return name of graph
	 */
	protected String parseGraphName(String commandLine) {
		Matcher matcher = GNRegex.matcher(commandLine);
		if (!matcher.find()) {
			return "";
		}
		graphName = matcher.group(1);
		return graphName;
	}

	/**
	 * parse EdgeType Command.The first command of this type will be adopted
	 * 
	 * @param commandLine
	 *            trimmed command line text. not null
	 * @return Classes of edge type
	 * @exception CommandException
	 *                throw when parse exception occurs
	 */
	protected List<Class<?>> parseEdgeType(String commandLine)
			throws CommandException {
		assert commandLine != null;
		List<Class<?>> res = new ArrayList<>();

		Matcher matcher = ETRegex.matcher(commandLine);
		if (!matcher.find())
			return res;
		// get all type names
		String[] types = matcher.group("types").split(",");
		for (int i = 0; i < types.length; i++)
			types[i] = types[i].trim();

		// get all classes
		for (int i = 0; i < types.length; i++) {
			String classname = "edge." + types[i];
			try {
				Class<?> cls = Class.forName(classname);
				if (GraphWhiteListHelper.isEdgeAdoptable(cls, graphType))
					res.add(cls);
				else {
					String logText = "Edge Class \"" + classname
							+ "\" Is Not Adoptable For GraphType \""
							+ graphType.getName() + "\"";
					CommandException exception = new CommandException(logText);
					exception.setStartPos(commandLine.indexOf(types[i]));
					// logger.info(exception.getLogText());
					throw exception;
				}
			} catch (ClassNotFoundException e) {
				// log if the class not found
				String logText = "Edge Class \"" + classname + "\" Not Found!";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(commandLine.indexOf(types[i]));
				// logger.info(exception.getLogText());
				throw exception;
			}
		}

		edgeTypes.addAll(res);
		return res;
	}

	/**
	 * parse and build edges from content
	 * 
	 * @param commandLine
	 *            trimmed command line text
	 * @return edge generated
	 * @exception CommandException
	 *                throw when exception occurs in parse or build stage
	 */
	@SuppressWarnings("unchecked")
	protected Edge parseEdgeBuild(String commandLine) throws CommandException {
		Edge res = null;

		Matcher matcher = EBRegex.matcher(commandLine);
		if (matcher.find()) {
			String label = matcher.group("label");
			// try to parse class
			Class<?> type = null;
			String className = "edge." + matcher.group("type");
			try {
				type = Class.forName(className);
			} catch (ClassNotFoundException e) {
				String logText = "Edge Class \"" + className + "\" Not Found";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("type"));
				// logger.info(exception.getLogText());
				throw exception;
			}
			// check if the type in the type list

			// try to get weight
			double weight;
			try {
				weight = Double.valueOf(matcher.group("weight"));
			} catch (NumberFormatException e) {
				String logText = "Invalid Weight \"" + matcher.group("weight")
						+ "\"";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("weight"));
				// logger.info(exception.getLogText());
				throw exception;
			}

			// try to get startVertex
			String startVertexLabel = matcher.group("startLabel");
			Vertex startVertex = vertices.get(startVertexLabel);

			if (null == startVertex) {
				String logText = "Vertex with Label \"" + startVertexLabel
						+ "\" Not Declared";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("startLabel"));
				// logger.info(exception.getLogText());
				throw exception;
			}
			// try to get endVertex
			String endVertexLabel = matcher.group("endLabel");
			Vertex endVertex = vertices.get(endVertexLabel);

			if (null == endVertex) {
				String logText = "Vertex with Label \"" + endVertexLabel
						+ "\" Not Declared";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("endLabel"));
				// logger.info(exception.getLogText());
				throw exception;
			}

			// construct edge
			try {
				// change label
				int index = 0;
				String originLabel = label;
				while (edges.contains(Edge.common(label))) {
					index++;
					label = label + "_" + index;
				}
				if (index != 0) {
					// logger.warning("Change Edge Label \"" + originLabel
					// + "\" To \"" + label + "\"");
				}

				Edge edge = (Edge) type.getDeclaredMethod("wrap", String.class,
						List.class, double.class).invoke(null, label,
								Arrays.asList(startVertex, endVertex), weight);
				// wrap failure is because passed arguments
				if (null == edge) {
					String logText = "Invalid Aruguments for Edge Class \""
							+ className + "\"; Edge \"" + label
							+ "\" Will Not Be Added";
					CommandException exception = new CommandException(logText);
					exception.setStartPos(0);
					exception.setIgnorable(true);
					// logger.info(exception.getLogText());
					throw exception;
				}

				Graph<Vertex, Edge> graph = null;
				try {
					graph = (Graph<Vertex, Edge>) graphType
							.getDeclaredConstructor(String.class)
							.newInstance("test");
				} catch (InstantiationException e) {
					assert false;
				}

				if (!graph.allowDuplicateEdge() && this.isDuplicateEdge(edge)) {
					String logText = "Duplicated Edge \"" + edge.getLabel()
							+ "\" Would Not Be Added";
					CommandException exception = new CommandException(logText);
					exception.setIgnorable(true);
					// logger.info(exception.getLogText());
					throw exception;
				}

				if (!graph.allowLoop() && edge.isLoop()) {
					String logText = "Loop Edge \"" + edge.getLabel()
							+ "\" Would Not Be Added";
					CommandException exception = new CommandException(logText);
					exception.setIgnorable(true);
					// logger.info(exception.getLogText());
					throw exception;
				}

				res = edge;
				edges.add(res);

				String directedText = matcher.group("weighted");
				if (edge.isDirected() ^ directedText.equals("Yes")) {
					String logText = "Edge \"" + edge.getLabel()
							+ "\" Has Been Changed To ";
					if (edge.isDirected())
						logText += "Directed";
					else
						logText += "Indirected";
					CommandException exception = new CommandException(logText);
					exception.setIgnorable(true);
					exception.setStartPos(matcher.start("weighted"));
					// logger.info(exception.getLogText());
					throw exception;
				}

			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				String logText = "Edge Class \"" + className
						+ "\" Doesn't Have Accessable static Method \"wrap\" with Correct Argument Types";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("type"));
				// logger.info(exception.getLogText());
				throw exception;
			}
		}

		return res;
	}

	/**
	 * parse and build HyperEdges from content
	 * 
	 * @param content
	 *            trimmed command line text
	 * @return list of edge generated
	 * @exception CommandException
	 *                throw when exception occurs in parse or build stage
	 */
	@SuppressWarnings("unchecked")
	protected Edge parseHyperEdgeBuild(String content) throws CommandException {
		Edge res = null;

		if (graphType == null) {
			String logText = "GraphType Not Specified";
			CommandException exception = new CommandException(logText);
			// logger.info(exception.getLogText());
			throw exception;
		}

		Matcher matcher = HEBRegex.matcher(content);
		if (matcher.find()) {
			Graph<Vertex, Edge> tempGraph = null;
			try {
				tempGraph = (Graph<Vertex, Edge>) graphType
						.getDeclaredConstructor(String.class)
						.newInstance("temp");
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e1) {
				assert false;
			}

			// check if graph allow hyper edge
			if (!tempGraph.allowHyperEdge()) {
				String logText = "HyperEdge Not Allowed In Graph Type \""
						+ graphType.getName() + "\"";
				CommandException exception = new CommandException(logText);
				exception.setIgnorable(true);
				// logger.info(exception.getLogText());
				throw exception;
			}

			String label = matcher.group("label");
			String className = "edge." + matcher.group("type");
			Class<?> type;
			// get type
			try {
				type = Class.forName(className);
			} catch (ClassNotFoundException e) {
				String logText = "HyperEdge Class \"" + className
						+ "\" Not Found";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("type"));
				// logger.info(exception.getLogText());
				throw exception;
			}

			String[] vertexLabels = matcher.group("vertices").split(",");
			// get vertexs in vertices
			List<Vertex> verts = new ArrayList<>();
			for (String vertLabel : vertexLabels) {
				Vertex vertex = vertices.get(vertLabel.trim());
				if (null == vertex) {
					String logText = "Vertex with Label \"" + vertLabel
							+ "\" Not Declared";
					CommandException exception = new CommandException(logText);
					int exceptionPos = matcher.group("vertices")
							.indexOf(vertLabel) + matcher.start("vertices");
					exception.setStartPos(exceptionPos);
					// logger.info(exception.getLogText());
					throw exception;
				}
				verts.add(vertex);
			}

			// construct edge
			Edge edge = null;
			try {
				edge = (Edge) type.getDeclaredMethod("wrap", String.class,
						List.class, double.class)
						.invoke(null, label, verts, -1);
				if (null == edge) {
					String logText = "Invalid Aruguments for HyperEdge Class \""
							+ className + "\"; HyperEdge \"" + label
							+ "\" Will Not be added";
					CommandException exception = new CommandException(logText);
					exception.setIgnorable(true);
					exception.setStartPos(0);
					// logger.info(exception.getLogText());
					throw exception;
				}
				res = edge; // add to res
				edges.add(res);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				String logText = "Edge Class \"" + className
						+ "\" Doesn't Have Accessable static Method \"wrap\" with Correct Argument Types";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("type"));
				// logger.info(exception.getLogText());
				throw exception;
			}
		}

		return res;
	}

	/**
	 * parse VertexTypes from input text
	 * 
	 * @param content
	 *            trimmed command line text
	 * @return classes of vertex allowed in target graph
	 * @exception CommandException
	 *                throw when parse exception occurs
	 */
	protected List<Class<?>> parseVertexType(String content)
			throws CommandException {
		List<Class<?>> res = new ArrayList<>();

		Matcher matcher = VTRegex.matcher(content);
		if (!matcher.find())
			return res;
		String[] typeNames = matcher.group("types").split(",");
		for (String typeName : typeNames) {
			typeName = typeName.trim();
			String className = "vertex." + typeName;
			try {
				Class<?> clazz = Class.forName(className);
				if (null != clazz)
					res.add(clazz);
			} catch (ClassNotFoundException e) {
				String logText = "Vertex Type \"" + className + "\" Not Found";
				CommandException exception = new CommandException(logText);
				int startPos = matcher.start("types")
						+ matcher.group("types").indexOf(typeName);
				exception.setStartPos(startPos);
				// logger.info(exception.getLogText());
				throw exception;
			}
		}

		vertexTypes.addAll(res);
		return res;
	}

	/**
	 * parse and build vertices from content
	 * 
	 * @param content
	 *            trimmed command line text
	 * @return list of generated vertices
	 * @exception CommandException
	 *                throw when exception occurs in parse or build stage
	 */
	protected Vertex parseVertexBuild(String content) throws CommandException {
		Vertex res = null;

		Matcher matcher = VBRegex.matcher(content);
		if (matcher.find()) {
			String label = matcher.group("label");
			// try to get vertex class
			Class<?> type = null;
			String className = "vertex." + matcher.group("type");
			try {
				type = Class.forName(className);
				// only types in vertexTypes can be used
				if (!vertexTypes.contains(type)) {
					String logText = "Vertex Type \"" + className
							+ "\" Not Declared";
					CommandException exception = new CommandException(logText);
					exception.setStartPos(matcher.start("type"));
					// logger.info(exception.getLogText());
					throw exception;
				}

			} catch (ClassNotFoundException e) {
				String logText = "Vertex Type \"" + className + "\" Not Found";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("type"));
				// logger.info(exception.getLogText());
				throw exception;
			}

			// get all args of vertex
			String[] args = new String[0];
			if (null != matcher.group("args")) {
				args = matcher.group("args").split(",");
				for (int i = 0; i < args.length; i++)
					args[i] = args[i].trim();
			}

			// construct vertex
			try {
				Vertex vertex = (Vertex) type
						.getDeclaredMethod("wrap", String.class, String[].class)
						.invoke(null, label, args);
				if (null == vertex) {
					String logText = "Invalid Aruguments for Vertex Class \""
							+ className + "\"; Vertex \"" + label
							+ "\" Build Failed";
					CommandException exception = new CommandException(logText);
					exception.setStartPos(0);
					// logger.info(exception.getLogText());
					throw exception;
				}
				// Not allowed in GraphType
				if (!GraphWhiteListHelper.isVertexAdoptable(vertex.getClass(),
						graphType)) {
					String logText = "Invalid Vertex Class \"" + type.getName()
							+ "\" for GraphType \"" + graphType.getName()
							+ "\"";
					CommandException exception = new CommandException(logText);
					exception.setStartPos(matcher.start("type"));
					// logger.info(exception.getLogText());
					throw exception;
				}
				res = vertex;
				vertices.put(res.getLabel(), res);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				String logText = "Vertex Class \"" + className
						+ "\" Doesn't Have Accessable static Method \"wrap\" with Correct Argument Types";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("type"));
				// logger.info(exception.getLogText());
				throw exception;
			}
		}

		return res;
	}

	/**
	 * execute a command that matches vertex --add ... ; to add a vertex to
	 * this.graph; Syntax : vertex --add label type [args..]. this.graph must be
	 * initialized
	 * 
	 * @param graph
	 *            to execute command on
	 * @param command
	 *            command to execute
	 * @return return true if succeed.
	 * @exception CommandException
	 *                raise when command parse or execute failed
	 */
	protected static boolean executeVertexAdd(Graph<Vertex, Edge> graph,
			String command) throws CommandException {
		Matcher matcher = VARegex.matcher(command);
		if (!matcher.find()) {
			String logText = "Illegal Argument";
			CommandException exception = new CommandException(logText);
			// only set startPos. the command and lineNo field should be set in
			// executeCommand method
			exception.setStartPos(0);
			// logger.info(exception.getLogText());
			throw exception;
		}

		String classname = "";
		String label = null;
		try {
			// get arguments of command
			label = matcher.group("label");
			String typename = matcher.group("type");
			String[] args = matcher.group("args").trim().split("\\s+");
			classname = "vertex." + typename;
			// create vertex
			Class<?> clazz = Class.forName(classname);
			Vertex vertex = (Vertex) clazz
					.getDeclaredMethod("wrap", String.class, String[].class)
					.invoke(null, label, args);
			if (null == vertex || !graph.addVertex(vertex)) {
				String logText = "Invalid Arguments for \"" + classname + "\"";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("args"));
				// logger.info(exception.getLogText());
				throw exception;
			}
		} catch (ClassNotFoundException e) {
			String logText = "Vertex Type \"" + classname + "\" Not Found.";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(matcher.start("type"));
			// logger.info(exception.getLogText());
			throw exception;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			// reflect call failed
			String logText = "Vertex Type \"" + classname
					+ "\" has no accessible static wrap(String, String[]) method";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(matcher.start("type"));
			// logger.info(exception.getLogText());
			throw exception;

		}

		assert label != null;
		// logger.fine("Vertex \"" + label + "\" Added Successfully");
		return true;
	}

	/**
	 * execute a command that matches vertex --delete ... ; to delete a vertex
	 * whose label match regex; Syntax : vertex --delete regex. this.graph must
	 * be initialized
	 * 
	 * @param graph
	 *            to execute command on
	 * @param command
	 *            command to execute
	 * @return return true if succeed.
	 * @exception CommandException
	 *                raise when command parse or execute failed
	 */
	protected static boolean executeVertexDelete(Graph<Vertex, Edge> graph,
			String command) throws CommandException {
		Matcher matcher = VDRegex.matcher(command);
		if (!matcher.find()) {
			String logText = "Illegal Argument";
			CommandException exception = new CommandException(logText);
			// only set startPos. the command and lineNo field should be set in
			// executeCommand method
			exception.setStartPos(0);
			// logger.info(exception.getLogText());
			throw exception;
		}

		String regex = "^" + matcher.group("regex") + "$";
		int deletedNum = 0;
		// delete all vertices matched without hesitation
		for (Vertex vertex : graph.vertices()) {
			try {
				if (Pattern.matches(regex, vertex.getLabel()))
					if (!graph.removeVertex(vertex)) {
						String logText = "Fail to Remove Vertex \""
								+ vertex.getLabel() + "\".";
						CommandException exception = new CommandException(
								logText);
						exception.setStartPos(matcher.start("regex"));
						// logger.info(exception.getLogText());
						throw exception;
					}
				deletedNum++;
			} catch (PatternSyntaxException e) {
				// fail if the given regex is invalid
				String logText = "Invalid regex \"" + regex + "\"";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("regex"));
				// logger.info(exception.getLogText());
				throw exception;
			}
		}

		// logger.fine(deletedNum + "Vertices Deleted Successfully");
		return true;
	}

	/**
	 * execute a command that matches edge --add ... ; to add an edge to
	 * this.graph; Syntax : edge --add label type [weight] v1, v2 . this.graph
	 * must be initialized
	 * 
	 * @param graph
	 *            to execute command on
	 * @param command
	 *            command to execute
	 * @return return true if succeed. else return false
	 * @exception CommandException
	 *                raise when command parse or execute failed
	 */
	protected static boolean executeEdgeAdd(Graph<Vertex, Edge> graph,
			String command) throws CommandException {
		Matcher matcher = EARegex.matcher(command);
		if (!matcher.find()) {
			String logText = "Illegal Argument";
			CommandException exception = new CommandException(logText);
			// only set startPos. the command and lineNo field should be set in
			// executeCommand method
			exception.setStartPos(0);
			// logger.info(exception.getLogText());
			throw exception;
		}

		String classname = "";
		String label = null;
		try {
			label = matcher.group("label");
			String typename = matcher.group("type");
			classname = "edge." + typename;
			Class<?> clazz = Class.forName(classname);

			// get weight
			String weightStr = matcher.group("weight");
			double weight = 0;
			if (null == weightStr)
				weight = -1;
			else {
				try {
					weight = Double.valueOf(weightStr);
				} catch (NumberFormatException e) {
					// weight is not double value
					String logText = "Invalid Weight Argument \"" + weightStr
							+ "\".";
					CommandException exception = new CommandException(logText);
					exception.setStartPos(matcher.start("weight"));
					// logger.info(exception.getLogText());
					throw exception;
				}
			}

			String vertexLabel1 = matcher.group("v1");
			String vertexLabel2 = matcher.group("v2");
			// get vertex in graph
			if (!graph.containVertex(Vertex.common(vertexLabel1))) {
				String logText = "Vertex \"" + vertexLabel1 + "\" Not in Graph "
						+ graph.getName();
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("v1"));
				// logger.info(exception.getLogText());
				throw exception;
			}
			if (!graph.containVertex(Vertex.common(vertexLabel2))) {
				String logText = "Vertex \"" + vertexLabel2 + "\" Not in Graph "
						+ graph.getName();
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("v2"));
				// logger.info(exception.getLogText());
				throw exception;
			}
			Set<Vertex> vertices = graph.vertices();
			Vertex v1 = null;
			Vertex v2 = null;
			for (Vertex vertex : vertices) {
				if (v1 == null && vertex.getLabel().equals(vertexLabel1))
					v1 = vertex.clone();
				if (v2 == null && vertex.getLabel().equals(vertexLabel2))
					v2 = vertex.clone();
				if (v1 != null && v2 != null)
					break;
			}

			Edge edge = (Edge) clazz
					.getDeclaredMethod("wrap", String.class, List.class,
							double.class)
					.invoke(null, label, Arrays.asList(v1, v2), weight);
			if (edge == null || !graph.addEdge(edge)) {
				String logText = "Can't add Edge \"" + label + "\" to Graph \""
						+ graph.getClass().getName() + "\"";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(0);
				// logger.info(exception.getLogText());
				throw exception;
			}
		} catch (ClassNotFoundException e) {
			String logText = "Edge Type \"" + classname + "\" Not Found";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(matcher.start("type"));
			throw exception;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			String logText = "Edge Type \"" + classname
					+ "\" has no accessible static wrap(String, List, double) method";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(matcher.start("type"));
			logger.info(exception.getLogText());
			throw exception;
		}

		// logger.fine("Edge \"" + label + "\" Added Successfully");
		return true;
	}

	/**
	 * execute a command that matches edge --delete ... ; to delete an edge from
	 * this.graph; Syntax : edge --delete regex . this.graph must be initialized
	 * 
	 * @param graph
	 *            to execute command on
	 * @param command
	 *            command to execute
	 * @return return true if succeed.
	 * @exception CommandException
	 *                raise when command parse or execute failed
	 */
	protected static boolean executeEdgeDelete(Graph<Vertex, Edge> graph,
			String command) throws CommandException {
		Matcher matcher = EDRegex.matcher(command);
		if (!matcher.find()) {
			String logText = "Illegal Argument";
			CommandException exception = new CommandException(logText);
			// only set startPos. the command and lineNo field should be set in
			// executeCommand method
			exception.setStartPos(0);
			// logger.info(exception.getLogText());
			throw exception;
		}

		String regex = matcher.group("regex");
		// delete all vertices matched without hesitation
		for (Edge edge : graph.edges()) {
			try {
				if (Pattern.matches(regex, edge.getLabel()))
					// log the fail information to screen
					if (!graph.removeEdge(edge)) {
						String logText = "Fail to Remove Edge \""
								+ edge.getLabel() + "\".";
						CommandException exception = new CommandException(
								logText);
						exception.setStartPos(matcher.start("regex"));
						throw exception;
					}
			} catch (PatternSyntaxException e) {
				// fail if the given regex is invalid
				String logText = "Invalid regex \"" + regex + "\"";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(matcher.start("regex"));
				// logger.info(exception.getLogText());
				throw exception;
			}
		}

		return true;
	}

	/**
	 * execute a command that matches hyperedge --add ... ; to add a hyperedge
	 * to this.graph; Syntax : hyperedge --add label type v1, ..., vn .
	 * this.graph must be initialized
	 * 
	 * @param graph
	 *            to execute command on
	 * @param command
	 *            command to execute
	 * @return return true if succeed.
	 * @exception CommandException
	 *                raise when command parse or execute failed
	 */
	protected static boolean executeHyperEdgeAdd(Graph<Vertex, Edge> graph,
			String command) throws CommandException {
		Matcher matcher = HEARegex.matcher(command);
		if (!matcher.find()) {
			String logText = "Illegal Argument";
			CommandException exception = new CommandException(logText);
			// only set startPos. the command and lineNo field should be set in
			// executeCommand method
			exception.setStartPos(0);
			// logger.info(exception.getLogText());
			throw exception;
		}

		String classname = "";
		try {
			String label = matcher.group("label");
			String typename = matcher.group("type");
			classname = "edge." + typename;
			Class<?> clazz = Class.forName(classname);

			String[] vertexLabels = matcher.group("vertices").trim()
					.split("\\s*,\\s*");
			// get vertex in graph
			for (String vertexLabel : vertexLabels)
				if (!graph.containVertex(Vertex.common(vertexLabel))) {
					String logText = "Vertex \"" + vertexLabel
							+ "\" Not in Graph \"" + graph.getName() + "\"";
					CommandException exception = new CommandException(logText);
					int startPos = matcher.start("vertices")
							+ matcher.group("vertices").indexOf(vertexLabel);
					exception.setStartPos(startPos);
					// logger.info(exception.getLogText());
					throw exception;
				}

			Set<Vertex> vertexSet = graph.vertices();
			Set<String> labels = new HashSet<>(Arrays.asList(vertexLabels));
			List<Vertex> vertices = new ArrayList<>();

			for (Vertex vertex : vertexSet) {
				if (labels.contains(vertex.getLabel()))
					vertices.add(vertex);
			}

			Edge edge = (Edge) clazz.getDeclaredMethod("wrap", String.class,
					List.class, double.class).invoke(null, label, vertices, -1);
			if (edge == null || !graph.addEdge(edge)) {
				String logText = "Can't Add Edge \"" + edge.getLabel()
						+ "\" of Type \"" + classname + "\" to Graph \""
						+ graph.getName() + "\"";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(0);
				// logger.info(exception.getLogText());
				throw exception;
			}
		} catch (ClassNotFoundException e) {
			String logText = "Edge Type \"" + classname + "\" Not Found";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(matcher.start("type"));
			// logger.info(exception.getLogText());
			return false;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			String logText = "Edge Type \"" + classname
					+ "\" has no accessible static wrap(String, List, double) method";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(matcher.start("type"));
			// logger.info(exception.getLogText());
			throw exception;
		}

		return true;
	}

	/**
	 * Build Graph from parsed commands
	 * 
	 * @return generated Graph
	 * @exception CommandException
	 *                throw when exception occurs when command execution
	 */
	protected ConcreteGraph build() throws CommandException {
		try {
			graph = (ConcreteGraph) graphType.getConstructor(String.class)
					.newInstance(graphName);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			assert false;
		}
		for (Vertex vertex : this.vertices.values())
			graph.addVertex(vertex);

		if (graph instanceof SocialNetwork) {
			for (Edge edge : this.edges) {
				assert edge != null;
				((SocialNetwork) graph).addEdgeDirect(edge);
			}
			return graph;
		}
		for (Edge edge : this.edges) {
			assert edge != null;
			graph.addEdge(edge);
		}
		return graph;
	}

	/**
	 * build Graph from content. the invalid commands will be ignored and logged
	 * 
	 * @param content
	 *            text to parse. every command in the text must in one line.
	 * @return generated Graph. if the graph type isn't specified, null will be
	 *         returned
	 * @exception CommandException
	 *                raise when parse or build failed
	 */
	public static ConcreteGraph buildGraph(String content)
			throws CommandException {
		ParseCommandHelper helper = new ParseCommandHelper();

		List<String> lines = Arrays.asList(content.split("(\n|\r\n)"));
		for (int lineNo = 0; lineNo < lines.size(); lineNo++) {
			String line = lines.get(lineNo).trim();
			// skip empty lines
			if (line.isEmpty())
				continue;
			try {
				if (helper.parseGraphType(line) != null)
					continue;
				if (!helper.parseGraphName(line).isEmpty())
					continue;
				if (!helper.parseVertexType(line).isEmpty())
					continue;
				if (helper.parseVertexBuild(line) != null)
					continue;
				if (!helper.parseEdgeType(line).isEmpty())
					continue;
				if (helper.parseEdgeBuild(line) != null)
					continue;
				if (helper.parseHyperEdgeBuild(line) != null)
					continue;
				String logText = "Invalid Syntax.";
				CommandException exception = new CommandException(logText);
				exception.setStartPos(0);
				exception.setLineNo(lineNo);
				exception.setCommand(line);
				System.out.println(exception.getOutputText());
				throw exception;
			} catch (CommandException e) {
				e.setLineNo(lineNo);
				e.setCommand(line);
				if (e.isIgnorable()) {
					System.out.println(e.getOutputText());
					continue;
				}
				throw e;
			}
		}
		helper.build();
		return helper.getGraph();
	}

	// public static ConcreteGraph buildGraphFromFile(String pathname)
	// throws IOException, CommandException {
	// File file = new File(pathname);
	// char[] cbuf = new char[(int) file.length()];
	// FileReader reader = new FileReader(file);
	// reader.read(cbuf);
	// reader.close();
	// return buildGraph(String.valueOf(cbuf));
	// }

	/**
	 * build Graph from File. the invalid commands will be ignored and logged
	 * 
	 * @param pathname
	 *            pathname of file to parse
	 * @return generated Graph. if the graph type isn't specified, null will be
	 *         returned
	 * @exception IOException
	 *                raise when the file not exists or can't be read
	 * @exception CommandException
	 *                raise when parse or build failed
	 */
	public static ConcreteGraph buildGraphFromFile(String pathname)
			throws IOException, CommandException {
		ParseCommandHelper helper = new ParseCommandHelper();
		reader.setFile(new File(pathname));

		String line;
		int lineNo = -1;
		while ((line = reader.readLine()) != null) {
			lineNo += 1;
			line = line.trim();
			// skip empty lines
			if (line.isEmpty())
				continue;
			// try {
			if (helper.parseGraphType(line) != null)
				continue;
			if (!helper.parseGraphName(line).isEmpty())
				continue;
			if (!helper.parseVertexType(line).isEmpty())
				continue;
			if (helper.parseVertexBuild(line) != null)
				continue;
			if (!helper.parseEdgeType(line).isEmpty())
				continue;
			if (helper.parseEdgeBuild(line) != null)
				continue;
			if (helper.parseHyperEdgeBuild(line) != null)
				continue;
			String logText = "Invalid Syntax.";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(0);
			exception.setLineNo(lineNo);
			exception.setCommand(line);
			System.out.println(exception.getOutputText());
			throw exception;
			// } catch (CommandException e) {
			// e.setLineNo(lineNo);
			// e.setCommand(line);
			// if (e.isIgnorable()) {
			// System.out.println(e.getOutputText());
			// continue;
			// }
			// throw e;
			// }
		}
		helper.build();
		return helper.getGraph();
	}

	/**
	 * parses a command and execute it on graph.
	 * 
	 * @param graph
	 *            Graph to operate
	 * @param command
	 *            command to parse and execute
	 * @exception CommandException
	 *                raise when error occurs when parse or execute command
	 */
	public static void parseAndExecuteCommand(Graph<Vertex, Edge> graph,
			String command) throws CommandException {
		String[] args = command.trim().split("\\s+");
		if (args.length < 2) {
			String logText = "Invalid Syntax";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			// logger.info(logText);
			throw exception;
		}
		switch (args[0]) {
			case "vertex" :
				if (args[1].equals("--add")) {
					executeVertexAdd(graph, command);
					break;
				} else if (args[1].equals("--delete")) {
					executeVertexDelete(graph, command);
					break;
				} else {
					String logText = "Unknown argument " + args[1]
							+ " for vertex";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					throw exception;
				}
			case "edge" :
				if (args[1].equals("--add")) {
					executeEdgeAdd(graph, command);
					break;
				} else if (args[1].equals("--delete")) {
					executeEdgeDelete(graph, command);
					break;
				} else {
					String logText = "Unknown argument " + args[1]
							+ " for vertex";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					throw exception;
				}
			case "hyperedge" :
				if (args[1].equals("--add")) {
					executeHyperEdgeAdd(graph, command);
					break;
				} else {
					String logText = "Unknown argument " + args[1]
							+ " for vertex";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					throw exception;
				}
			default :
				String logText = "Unknown command " + args[0] + " for vertex";
				CommandException exception = new CommandException(logText);
				exception.setLineNo(0);
				exception.setStartPos(0);
				exception.setCommand(command);
				// logger.info(logText);
				throw exception;
		}
	}

	/**
	 * check if an edge is duplicated in the graph. duplicate means it has the
	 * same target and source as other edge. invalid to hyperedge
	 * 
	 * @param edge
	 *            edge to check
	 * @return return true if graph has edge duplicated to param edge
	 */
	private boolean isDuplicateEdge(Edge edge) {
		Set<Vertex> vertices = edge.vertices();
		for (Edge edge2 : this.edges) {
			if (!edge2.isDirected() && vertices.containsAll(edge2.vertices()))
				return true;
			else if (edge2.isDirected()
					&& edge.targetVertices().containsAll(edge2.targetVertices())
					&& edge.sourceVertices()
							.containsAll(edge2.sourceVertices()))
				return true;
			else
				return false;
		}
		return false;
	}
}
