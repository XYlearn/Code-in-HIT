package helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edge.Edge;
import graph.Graph;
import helper.writer.FileWriteStrategy;
import helper.writer.WriterFileWriter;
import util.ParseUtil;
import vertex.Vertex;

/**
 * Class to store graph to a File with the similar grammer of ParseCommandHelper
 */
public class GraphStoreHelper {

	protected String graphNameExp;
	protected String graphTypeExp;
	protected String vertexTypeExp;
	protected String edgeTypeExp;

	protected List<String> vertexExps;
	protected List<String> edgeExps;
	protected List<String> hyperEdgeExps;

	protected Set<String> vertexTypes;
	protected Set<String> edgeTypes;

	protected static FileWriteStrategy writer = new WriterFileWriter();

	/**
	 * set writer
	 * 
	 * @param writer
	 *            writer to use
	 * @return return true if success
	 */
	public static boolean setWriter(FileWriteStrategy writer) {
		if (null == writer)
			return false;
		GraphStoreHelper.writer = writer;
		return true;
	}

	/**
	 * Constructor of GraphStoreHelper
	 */
	protected GraphStoreHelper() {
		graphNameExp = "";
		graphTypeExp = "";
		vertexTypeExp = "";
		edgeTypeExp = "";
		vertexExps = new ArrayList<>();
		edgeExps = new ArrayList<>();
		hyperEdgeExps = new ArrayList<>();
		vertexTypes = new HashSet<>();
		edgeTypes = new HashSet<>();
	}

	/**
	 * get expression of graph name
	 * 
	 * @param graph
	 *            graph to get expression
	 * @return graph name expression
	 */
	protected String graphNameExpression(Graph<Vertex, Edge> graph) {
		StringBuilder builder = new StringBuilder(1024);
		builder.append("GraphName = ");
		builder.append(graph.getName());
		this.graphNameExp = builder.toString();
		return this.graphNameExp;
	}

	/**
	 * get expression of graph type
	 * 
	 * @param graph
	 *            graph to get expression
	 * @return graph type expression
	 */
	protected String graphTypeExpression(Graph<Vertex, Edge> graph) {
		StringBuilder builder = new StringBuilder(1024);
		builder.append("GraphType = ");
		String graphType = ParseUtil.getClassName(graph.getClass());
		builder.append(graphType);
		this.graphTypeExp = builder.toString();
		return this.graphTypeExp;
	}

	/**
	 * get expression of vertex build. and get all possible vertex types
	 * 
	 * @param vertex
	 *            vertex to get build expression from
	 * @return vertex build expression
	 */
	protected String vertexExpression(Vertex vertex) {
		StringBuilder builder = new StringBuilder(1024);
		builder.append("Vertex = <");
		builder.append(vertex.getLabel());
		builder.append(", ");

		// get vertex type
		String vertexType = ParseUtil.getClassName(vertex.getClass());
		builder.append(vertexType);
		vertexTypes.add(vertexType);

		builder.append(", <");
		List<String> info = vertex.getVertexInfo();
		for (int i = 0; i < info.size() - 1; i++) {
			builder.append(info.get(i));
			builder.append(", ");
		}
		if (info.size() >= 1)
			builder.append(info.get(info.size() - 1));
		builder.append(">>");
		String expression = builder.toString();
		this.vertexExps.add(expression);
		return expression;
	}

	/**
	 * get expression of edge build. and get all possible edge types
	 * 
	 * @param edge
	 *            edge to get build expression from, must be valid
	 * @return edge build expression
	 */
	protected String edgeExpression(Edge edge) {
		StringBuilder builder = new StringBuilder(1024);
		if (edge.isHyperEdge())
			builder.append("HyperEdge = <");
		else
			builder.append("Edge = <");
		builder.append(edge.getLabel());
		builder.append(", ");

		// get edge type
		String edgeType = ParseUtil.getClassName(edge.getClass());
		builder.append(edgeType);
		edgeTypes.add(edgeType);

		builder.append(", ");
		builder.append(String.valueOf(edge.getWeight()));
		builder.append(", ");
		// get directed edge source and target
		if (edge.isDirected()) {
			String v1 = edge.sourceVertices().iterator().next().getLabel();
			String v2 = edge.targetVertices().iterator().next().getLabel();
			builder.append(v1);
			builder.append(", ");
			builder.append(v2);
		}
		// get hyperedge vertices
		else if (edge.isHyperEdge()) {
			List<Vertex> vertices = new ArrayList<>(edge.vertices());
			for (int i = 0; i < vertices.size() - 1; i++) {
				builder.append(vertices.get(i));
				builder.append(", ");
			}
			builder.append(vertices.get(vertices.size() - 1));
		}
		// get undirected and non-hyperedge vertices
		else {
			Iterator<Vertex> iterator = edge.vertices().iterator();
			String v1 = iterator.next().getLabel();
			String v2 = iterator.next().getLabel();
			builder.append(v1);
			builder.append(", ");
			builder.append(v2);
		}
		builder.append(", ");
		if (edge.getWeight() >= 0)
			builder.append("Yes>");
		else
			builder.append("No>");

		String expression = builder.toString();
		if (edge.isHyperEdge())
			hyperEdgeExps.add(expression);
		else
			edgeExps.add(expression);

		return expression;
	}

	/**
	 * get vertex type expression from vertexTypes
	 * 
	 * @return vertex type expression
	 */
	protected String vertexTypeExpression() {
		StringBuilder builder = new StringBuilder(1024);
		builder.append("VertexType = ");
		List<String> vertexTypes = new ArrayList<>(this.vertexTypes);
		for (int i = 0; i < vertexTypes.size() - 1; i++) {
			builder.append(vertexTypes.get(i));
			builder.append(", ");
		}
		if (vertexTypes.size() >= 1)
			builder.append(vertexTypes.get(vertexTypes.size() - 1));
		this.vertexTypeExp = builder.toString();
		return this.vertexTypeExp;
	}

	/**
	 * get edge type expression from edgeTypes
	 * 
	 * @return edge type expression
	 */
	protected String edgeTypeExpression() {
		StringBuilder builder = new StringBuilder(1024);
		builder.append("EdgeType = ");
		List<String> edgeTypes = new ArrayList<>(this.edgeTypes);
		for (int i = 0; i < edgeTypes.size() - 1; i++) {
			builder.append(edgeTypes.get(i));
			builder.append(", ");
		}
		if (edgeTypes.size() >= 1)
			builder.append(edgeTypes.get(edgeTypes.size() - 1));
		this.edgeTypeExp = builder.toString();
		return this.edgeTypeExp;
	}

	/**
	 * construct all expressions from graph
	 * 
	 * @param graph
	 *            graph to construct expressions from
	 */
	protected void construct(Graph<Vertex, Edge> graph) {
		this.graphTypeExpression(graph);
		this.graphNameExpression(graph);
		for (Vertex vertex : graph.vertices())
			this.vertexExpression(vertex);
		for (Edge edge : graph.edges())
			this.edgeExpression(edge);
		this.vertexTypeExpression();
		this.edgeTypeExpression();
	}

	/**
	 * store a graph to file in defined grammar
	 * 
	 * @param graph
	 *            graph to store
	 * @param file
	 *            file to write
	 * @throws IOException
	 *             throw when the file not exists or can't be written
	 */
	public static void store(Graph<Vertex, Edge> graph, File file)
			throws IOException {
		// initialize work
		GraphStoreHelper helper = new GraphStoreHelper();
		writer.setFile(file);
		helper.construct(graph);

		writer.writeLine(helper.graphTypeExp);
		writer.writeLine(helper.graphNameExp);
		writer.writeLine(helper.vertexTypeExp);
		writer.writeLine(helper.edgeTypeExp);
		for (String expression : helper.vertexExps)
			writer.writeLine(expression);
		for (String expression : helper.edgeExps)
			writer.writeLine(expression);

		writer.close();
	}
}
