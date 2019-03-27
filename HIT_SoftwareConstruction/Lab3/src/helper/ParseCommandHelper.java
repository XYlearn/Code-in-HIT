package helper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;

import edge.Edge;
import graph.Graph;
import graph.ConcreteGraph;
import vertex.Vertex;

/**
 * Class to parse command from string content or file and parse and execute
 * command lines.
 */
public class ParseCommandHelper {

	/**
	 * AF: use Set<Class<?>> to save edgeTypes; use Class<?> to save graphType; use
	 * Set<Class<?>> to represents vertexTypes; use String to store graphName; use
	 * Set<Edge> to represent edges; use Map<Vertex> to represent vertices. All
	 * these were parsed from content
	 * 
	 * RI: element in vertices has class in vertexTypes. element in edges has class
	 * in edgeTypes. vertices in edge must in vertices
	 * 
	 * safety from rep exposure: only the generated graph can be accessed out of the
	 * class.
	 */

	protected final static Pattern GTRegex; // Graph Type regular expression
	protected final static Pattern GNRegex; // Graph Name regular expression
	protected final static Pattern ETRegex; // Edge Type regular expression
	protected final static Pattern EBRegex; // Edge Build regular expression
	protected final static Pattern HEBRegex; // Hyper Edge Build regular expression
	protected final static Pattern VTRegex; // Vertex Type regular expression
	protected final static Pattern VBRegex; // Vertex Build regular expression

	protected final static Pattern VARegex; // vertex add regular expression
	protected final static Pattern VDRegex; // vertex delete regular expression
	protected final static Pattern EARegex; // edge add regular expression
	protected final static Pattern EDRegex; // edge delete regular expression
	protected final static Pattern HEARegex; // hyperedge add regular expression

	/**
	 * build regex
	 */
	static {
		// Graph Type regular expression
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("GraphType[\\s]*=[\\s]*");
		stringBuilder.append("(?<type>[\\w]+)");
		GTRegex = Pattern.compile(stringBuilder.toString());

		// Graph Name regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("GraphName[\\s]*=[\\s]*");
		stringBuilder.append("(?<name>[\\w]+)");
		GNRegex = Pattern.compile(stringBuilder.toString());

		// Edge Type regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("EdgeType[\\s]*=[\\s]*");
		stringBuilder.append("(?<types>([\\w]+[\\s]*,[\\s]*)*[\\w]+)");
		ETRegex = Pattern.compile(stringBuilder.toString());

		// Edge Build regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("Edge[\\s]*=[\\s]*");
		stringBuilder.append("<[\\s]*(?<label>[\\w]+)[\\s]*,"); // label
		stringBuilder.append("[\\s]*(?<type>[\\w]+)[\\s]*,"); // type
		stringBuilder.append("[\\s]*(?<weight>-?[\\d]+(\\.[\\d]+)?)[\\s]*,"); // weight
		stringBuilder.append("[\\s]*(?<startLabel>[\\w]+)[\\s]*,"); // startVertex
		stringBuilder.append("[\\s]*(?<endLabel>[\\w]+)[\\s]*,"); // endVertex
		stringBuilder.append("[\\s]*(?<weighted>Yes|No)[\\s]*>"); // if it is weighted
		EBRegex = Pattern.compile(stringBuilder.toString());

		// Hyper Edge Build regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("HyperEdge[\\s]*=[\\s]*");
		stringBuilder.append("<[\\s]*(?<label>[\\w]+)[\\s]*,"); // label
		stringBuilder.append("[\\s]*(?<type>[\\w]+)[\\s]*,"); // type
		stringBuilder.append("[\\s]*\\{[\\s]*(?<vertices>(([\\w]+)[\\s]*,[\\s]*)*([\\w]+))"); // vertices
		stringBuilder.append("[\\s]*\\}>");
		HEBRegex = Pattern.compile(stringBuilder.toString());

		// Vertex Type regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("VertexType[\\s]*=[\\s]*");
		stringBuilder.append("(?<types>(([\\w]+)[\\s]*,[\\s]*)*([\\w]+))"); // types
		VTRegex = Pattern.compile(stringBuilder.toString());

		// Vertex Build regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("Vertex[\\s]*=[\\s]*");
		stringBuilder.append("<[\\s]*(?<label>[\\w]+)[\\s]*,"); // label
		stringBuilder.append("[\\s]*(?<type>[\\w]+)[\\s]*,"); // type
		stringBuilder.append("[\\s]*<((?<args>[\\s]*(.+?[\\s]*,[\\s]*)*.+?[\\s]*)>[\\s]*)?>"); // args
		VBRegex = Pattern.compile(stringBuilder.toString());

		// vertex add regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^\\s*vertex\\s+--add\\s+");
		stringBuilder.append("(?<label>\\w+)\\s+(?<type>\\w+)(?<args>(\\s+\\w+)*)\\s*$");
		VARegex = Pattern.compile(stringBuilder.toString());

		// vertex delete regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^\\s*vertex\\s+--delete\\s+");
		stringBuilder.append("\"(?<regex>.+?)\"\\s*$");
		VDRegex = Pattern.compile(stringBuilder.toString());

		// edge add regular expression
		stringBuilder = new StringBuilder();
		stringBuilder.append("^\\s*edge\\s+--add\\s+");
		stringBuilder.append("(?<label>\\w+)\\s+(?<type>\\w+)(\\s+(?<weight>-?[\\d]+(\\.[\\d]+)?))?\\s+");
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
		stringBuilder.append("(?<label>\\w+)\\s+(?<type>\\w+)\\s+(?<vertices>(\\w+\\s*,\\s*)*(\\w+))\\s*$");
		HEARegex = Pattern.compile(stringBuilder.toString());
	}

	private final Set<Class<?>> edgeTypes;
	private Class<?> graphType;
	private final Set<Class<?>> vertexTypes;
	private String graphName;
	private final Set<Edge> edges;
	private final Map<String, Vertex> vertices;
	private ConcreteGraph graph;

	/**
	 * constructor of ParseCommandHelper
	 */
	public ParseCommandHelper() {
		edgeTypes = new HashSet<>();
		vertexTypes = new HashSet<>();
		edges = new HashSet<>();
		vertices = new HashMap<>();
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
	 * parse GraphType Command.The first command of this type will be adopted. The
	 * command must be in format:
	 * <p>
	 * GraphType = "TYPENAME"[|"TYPENAME"..]
	 * </p>
	 * 
	 * @param content
	 *            input text
	 * @return return valid graph class
	 * @throws BadGraphTypeException
	 *             raise when no Graph type is specified
	 */
	protected Class<?> parseGraphType(String content) throws BadGraphTypeException {

		Matcher matcher = GTRegex.matcher(content);
		if (!matcher.find())
			return null;
		try {
			graphType = Class.forName("graph." + matcher.group(1));
			return graphType;
		} catch (ClassNotFoundException e) {
			log(0, "[-]Graph Type \"" + matcher.group(1) + "\" Not Found");
			throw new BadGraphTypeException();
		}
	}

	/**
	 * parse GraphName Command.The first command of this type will be adopted
	 * 
	 * @param content
	 *            input text
	 * @return name of graph
	 */
	protected String parseGraphName(String content) {
		Matcher matcher = GNRegex.matcher(content);
		if (!matcher.find()) {
			System.err.println("[!]Warning: graph name not specified");
			return "";
		}
		graphName = matcher.group(1);
		return graphName;
	}

	/**
	 * parse EdgeType Command.The first command of this type will be adopted
	 * 
	 * @param content
	 *            input text
	 * @return Classes of edge type
	 */
	protected List<Class<?>> parseEdgeType(String content) {
		List<Class<?>> res = new ArrayList<>();

		Matcher matcher = ETRegex.matcher(content);
		if (!matcher.find())
			return res;
		// get all type names
		String[] types = matcher.group("types").split(",");
		for (int i = 0; i < types.length; i++)
			types[i] = types[i].trim();

		// get all classes
		for (int i = 0; i < types.length; i++) {
			try {
				res.add(Class.forName("edge." + types[i]));
			} catch (ClassNotFoundException e) {
				// log if the class not found
				System.err.println("[!]Warning: " + types[i] + " Not Found!");
			}
		}

		edgeTypes.addAll(res);
		return res;
	}

	/**
	 * parse and build edges from content
	 * 
	 * @param content
	 *            input text
	 * @return list of edge generated
	 */
	protected List<Edge> parseEdgeBuild(String content) {
		List<Edge> res = new ArrayList<>();

		Matcher matcher = EBRegex.matcher(content);
		while (matcher.find()) {
			String label = matcher.group("label");
			// try to parse class
			Class<?> type = null;
			String className = "edge." + matcher.group("type");
			try {
				type = Class.forName(className);
			} catch (ClassNotFoundException e) {
				log(0, "[-]Edge Type \"" + className + "\" Not Found");
				continue;
			}
			// check if the type in the type list

			// try to get weight
			double weight;
			try {
				weight = Double.valueOf(matcher.group("weight"));
			} catch (NumberFormatException e) {
				log(0, "[-] Invalid weight " + matcher.group("weight"));
				continue;
			}

			// try to get startVertex
			String startVertexLabel = matcher.group("startLabel");
			Vertex startVertex = vertices.get(startVertexLabel);

			if (null == startVertex) {
				log(0, "[-]Vertex \"" + startVertexLabel + "\" not found");
				continue;
			}
			// try to get endVertex
			String endVertexLabel = matcher.group("endLabel");
			Vertex endVertex = vertices.get(endVertexLabel);

			if (null == endVertex) {
				log(0, "[-]Vertex \"" + endVertexLabel + "\" not found");
				continue;
			}

			// construct edge
			try {
				Edge edge = (Edge) type.getDeclaredMethod("wrap", String.class, List.class, double.class).invoke(null,
						label, Arrays.asList(startVertex, endVertex), weight);
				if (null == edge) {
					log(0, "[-]Fail to build edge " + label);
					continue;
				}
				res.add(edge);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				log(0, "Internal Exception in parseEdgeBuild");
			}
		}

		edges.addAll(res);
		return res;
	}

	/**
	 * parse and build HyperEdges from content
	 * 
	 * @param content
	 *            input text
	 * @return list of edge generated
	 */
	protected List<Edge> parseHyperEdgeBuild(String content) {
		List<Edge> res = new ArrayList<>();
		Matcher matcher = HEBRegex.matcher(content);
		while (matcher.find()) {
			String label = matcher.group("label");
			String className = "edge." + matcher.group("type");
			Class<?> type;
			try {
				type = Class.forName(className);
			} catch (ClassNotFoundException e) {
				log(0, "invalid HyperEdge Type " + className);
				continue;
			}

			String[] vertexLabels = matcher.group("vertices").split(",");
			// get vertexs in vertices
			List<Vertex> verts = new ArrayList<>();
			for (String vertLabel : vertexLabels) {
				Vertex vertex = vertices.get(vertLabel.trim());
				if (null == vertex) {
					log(0, "[-] Vertex " + vertLabel + "not found");
					continue;
				}
				verts.add(vertex);
			}

			// construct edge
			Edge edge = null;
			try {
				edge = (Edge) type.getDeclaredMethod("wrap", String.class, List.class, double.class).invoke(null, label,
						verts, -1);
				res.add(edge); // add to res
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				log(0, "Internal Exception");
				continue;
			}
		}
		edges.addAll(res);
		return res;
	}

	/**
	 * parse VertexTypes from input text
	 * 
	 * @param content
	 *            text input
	 * @return classes of vertex allowed in target graph
	 */
	protected List<Class<?>> parseVertexType(String content) {
		List<Class<?>> res = new ArrayList<>();

		Matcher matcher = VTRegex.matcher(content);
		if (!matcher.find())
			return res;
		String[] typeNames = matcher.group("types").split(",");
		for (String typeName : typeNames) {
			typeName = typeName.trim();
			String className = "vertex." + typeName;
			if (typeName.isEmpty())
				continue;
			try {
				res.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				log(0, "[-]Vertex Type \"" + className + "\" not found");
			}
		}

		vertexTypes.addAll(res);
		return res;
	}

	/**
	 * parse and build vertices from content
	 * 
	 * @param content
	 *            text input
	 * @return list of generated vertices
	 */
	protected List<Vertex> parseVertexBuild(String content) {
		List<Vertex> res = new ArrayList<>();

		Matcher matcher = VBRegex.matcher(content);
		while (matcher.find()) {
			String label = matcher.group("label");
			// try to get vertex class
			Class<?> type = null;
			String className = "vertex." + matcher.group("type");
			try {
				type = Class.forName(className);
				// only types in vertexTypes can be used
				if (!vertexTypes.contains(type))
					continue;
			} catch (Exception e) {
				log(0, "[-]Vertex Type \"" + className + "\" Not Found");
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
				Vertex vertex = (Vertex) type.getDeclaredMethod("wrap", String.class, String[].class).invoke(null,
						label, args);
				if (null != vertex)
					res.add(vertex);
				else
					log(0, "[-]Fail to build Vertex " + label);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				log(0, e.getMessage());
			}
		}
		// add all vertices to vertices
		for (Vertex vertex : res) {
			vertices.put(vertex.getLabel(), vertex);
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
	 * @return return true if succeed. else return false
	 */
	protected static boolean executeVertexAdd(Graph<Vertex, Edge> graph, String command) {
		Matcher matcher = VARegex.matcher(command);
		if (!matcher.find()) {
			log(0, "Illegal Argument");
			return false;
		}

		String classname = "";
		try {
			// get arguments of command
			String label = matcher.group("label");
			String typename = matcher.group("type");
			String[] args = matcher.group("args").trim().split("\\s+");
			classname = "vertex." + typename;
			// create vertex
			Class<?> clazz = Class.forName(classname);
			Vertex vertex = (Vertex) clazz.getDeclaredMethod("wrap", String.class, String[].class).invoke(null, label,
					args);
			if (null == vertex || !graph.addVertex(vertex)) {
				log(0, "Can't add that Vertex");
				return false;
			}
		} catch (ClassNotFoundException e) {
			log(0, "Vertex Type " + classname + " Not Found");
			return false;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * execute a command that matches vertex --delete ... ; to delete a vertex whose
	 * label match regex; Syntax : vertex --delete regex. this.graph must be
	 * initialized
	 * 
	 * @param graph
	 *            to execute command on
	 * @param command
	 *            command to execute
	 * @return return true if succeed. else return false
	 */
	protected static boolean executeVertexDelete(Graph<Vertex, Edge> graph, String command) {
		Matcher matcher = VDRegex.matcher(command);
		if (!matcher.find()) {
			log(0, "Illegal Argument");
			return false;
		}

		String regex = "^" + matcher.group("regex") + "$";
		// delete all vertices matched without hesitation
		for (Vertex vertex : graph.vertices()) {
			if (Pattern.matches(regex, vertex.getLabel()))
				// log the fail information to screen
				if (!graph.removeVertex(vertex))
					log(0, "Fail to remove Vertex " + vertex.getLabel());
		}

		return true;
	}

	/**
	 * execute a command that matches edge --add ... ; to add an edge to this.graph;
	 * Syntax : edge --add label type [weight] v1, v2 . this.graph must be
	 * initialized
	 * 
	 * @param graph
	 *            to execute command on
	 * @param command
	 *            command to execute
	 * @return return true if succeed. else return false
	 */
	protected static boolean executeEdgeAdd(Graph<Vertex, Edge> graph, String command) {
		Matcher matcher = EARegex.matcher(command);
		if (!matcher.find()) {
			log(0, "Illegal Argument");
			return false;
		}

		String classname = "";
		try {
			String label = matcher.group("label");
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
					log(0, "Illegal Argument");
					return false;
				}
			}

			String vertexLabel1 = matcher.group("v1");
			String vertexLabel2 = matcher.group("v2");
			// get vertex in graph
			if (!graph.containVertex(Vertex.common(vertexLabel1))) {
				log(0, "Vertex " + vertexLabel1 + " Not in Graph " + graph.getName());
				return false;
			}
			if (!graph.containVertex(Vertex.common(vertexLabel2))) {
				log(0, "Vertex " + vertexLabel2 + " Not in Graph " + graph.getName());
				return false;
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

			Edge edge = (Edge) clazz.getDeclaredMethod("wrap", String.class, List.class, double.class).invoke(null,
					label, Arrays.asList(v1, v2), weight);
			if (edge == null || !graph.addEdge(edge)) {
				log(0, "Can't add Edge " + label);
				return false;
			}
		} catch (ClassNotFoundException e) {
			log(0, "Edge Type " + classname + " Not Found");
			return false;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			return false;
		}

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
	 * @return return true if succeed. else return false
	 */
	protected static boolean executeEdgeDelete(Graph<Vertex, Edge> graph, String command) {
		Matcher matcher = EDRegex.matcher(command);
		if (!matcher.find()) {
			log(0, "Illegal Argument");
			return false;
		}

		String regex = matcher.group("regex");
		// delete all vertices matched without hesitation
		for (Edge edge : graph.edges()) {
			if (Pattern.matches(regex, edge.getLabel()))
				// log the fail information to screen
				if (!graph.removeEdge(edge))
					log(0, "Fail to remove Edge " + edge.getLabel());
		}

		return true;
	}

	/**
	 * execute a command that matches hyperedge --add ... ; to add a hyperedge to
	 * this.graph; Syntax : hyperedge --add label type v1, ..., vn . this.graph must
	 * be initialized
	 * 
	 * @param graph
	 *            to execute command on
	 * @param command
	 *            command to execute
	 * @return return true if succeed. else return false
	 */
	protected static boolean executeHyperEdgeAdd(Graph<Vertex, Edge> graph, String command) {
		Matcher matcher = HEARegex.matcher(command);
		if (!matcher.find()) {
			log(0, "Illegal Argument");
			return false;
		}

		String classname = "";
		try {
			String label = matcher.group("label");
			String typename = matcher.group("type");
			classname = "edge." + typename;
			Class<?> clazz = Class.forName(classname);

			String[] vertexLabels = matcher.group("vertices").trim().split("\\s*,\\s*");
			// get vertex in graph
			for (String vertexLabel : vertexLabels)
				if (!graph.containVertex(Vertex.common(vertexLabel))) {
					log(0, "Vertex " + vertexLabel + " Not in Graph " + graph.getName());
					return false;
				}

			Set<Vertex> vertexSet = graph.vertices();
			Set<String> labels = new HashSet<>(Arrays.asList(vertexLabels));
			List<Vertex> vertices = new ArrayList<>();

			for (Vertex vertex : vertexSet) {
				if (labels.contains(vertex.getLabel()))
					vertices.add(vertex);
			}

			Edge edge = (Edge) clazz.getDeclaredMethod("wrap", String.class, List.class, double.class).invoke(null,
					label, vertices, -1);
			if (edge == null || !graph.addEdge(edge)) {
				log(0, "Can't add Edge " + label);
				return false;
			}
		} catch (ClassNotFoundException e) {
			log(0, "Edge Type " + classname + " Not Found");
			return false;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * clear all information after last call to buildGraph
	 */
	protected void clear() {
		graphType = null;
		graphName = null;
		vertexTypes.clear();
		edgeTypes.clear();
		vertices.clear();
		edges.clear();
	}

	protected ConcreteGraph build() {
		try {
			graph = (ConcreteGraph) graphType.getConstructor(String.class).newInstance(graphName);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log(0, "[-]Internal Exception");
		}
		for (Vertex vertex : this.vertices.values())
			graph.addVertex(vertex);

		for (Edge edge : this.edges)
			graph.addEdge(edge);
		return graph;
	}

	/**
	 * log information in parse process
	 * 
	 * @param level
	 *            log level
	 * @param info
	 *            information to log
	 */
	protected static void log(int level, String info) {
		if (level == 0)
			System.err.println(info);
	}

	/**
	 * build Graph from content. the invalid commands will be ignored and logged
	 * 
	 * @param content
	 *            text to parse
	 * @return generated Graph. if the graph type isn't specified, null will be
	 *         returned
	 */
	public static ConcreteGraph buildGraph(String content) {
		ParseCommandHelper helper = new ParseCommandHelper();
		try {
			helper.parseGraphType(content);
		} catch (BadGraphTypeException e) {
			// if the graph type not specified, null will be returned
			return null;
		}
		helper.parseGraphName(content);
		helper.parseVertexType(content);
		helper.parseVertexBuild(content);
		helper.parseEdgeType(content);
		helper.parseEdgeBuild(content);
		helper.parseHyperEdgeBuild(content);
		helper.build();
		return helper.getGraph();
	}

	/**
	 * build Graph from File. the invalid commands will be ignored and logged
	 * 
	 * @param pathname
	 *            pathname of file to parse
	 * @return generated Graph. if the graph type isn't specified, null will be
	 *         returned
	 * @exception IOException
	 *                raise when the file not exists or can't be read
	 */
	public static ConcreteGraph buildGraphFromFile(String pathname) throws IOException {
		File file = new File(pathname);
		char[] cbuf = new char[(int) file.length()];
		FileReader reader = new FileReader(file);
		reader.read(cbuf);
		reader.close();
		return buildGraph(String.valueOf(cbuf));
	}

	/**
	 * parses a command and execute it on graph.
	 * 
	 * @param graph
	 *            Graph to operate
	 * @param command
	 *            command to parse and execute
	 */
	public static void parseAndExecuteCommand(Graph<Vertex, Edge> graph, String command) throws BadCommandException {
		String[] args = command.trim().split("\\s+");
		if(args.length < 2) {
			log(0, "Invalid Syntax");
		}
		switch (args[0]) {
		case "vertex":
			if (args[1].equals("--add")) {
				executeVertexAdd(graph, command);
				break;
			} else if (args[1].equals("--delete")) {
				executeVertexDelete(graph, command);
				break;
			} else {
				log(0, "Unknown argument " + args[1]);
			}
			break;
		case "edge":
			if (args[1].equals("--add")) {
				executeEdgeAdd(graph, command);
				break;
			} else if (args[1].equals("--delete")) {
				executeEdgeDelete(graph, command);
				break;
			} else {
				log(0, "Unknown argument " + args[1]);
			}
			break;
		case "hyperedge":
			if (args[1].equals("--add")) {
				executeHyperEdgeAdd(graph, command);
				break;
			} else {
				log(0, "Unknown argument " + args[1]);
			}
		default:
			log(0, "Unknow Command " + args[0]);
			break;
		}
	}

}

/**
 * Exception for no Graph type was specified
 */
class BadGraphTypeException extends Exception {
	private static final long serialVersionUID = 1L;
}

class BadCommandException extends Exception {
	private static final long serialVersionUID = 1L;

}
