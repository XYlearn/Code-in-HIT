package helper;

import java.lang.reflect.Field;
import java.util.List;

import edge.Edge;
import graph.Graph;
import vertex.Vertex;

/**
 * class to check if a vertex or edge is adoptable to graph. Graph classes that
 * want to filter Vertex types must define
 * {@code public static List<Class<? extends Vertex>> vertexWhiteList} ; The
 * Graph classes that want to filter Edge types must define
 * {@code public static List<Class<?> extends Edge> edgeWhiteList}
 */
public class GraphWhiteListHelper {
	/**
	 * check if edge is adoptable to graph
	 * 
	 * @param edge
	 *            edge to check
	 * @param graph
	 *            graph to adopt the edge
	 * @return return true if edge is adoptable by graph
	 */
	public static boolean isEdgeAdoptable(Edge edge, Graph<Vertex, Edge> graph) {
		return (isEdgeAdoptable(edge.getClass(), graph.getClass()));
	}

	/**
	 * check if vertex is adoptable to graph
	 * 
	 * @param vertex
	 *            vertex to check
	 * @param graph
	 *            graph to adopt the edge
	 * @return return true if vertex is adoptable by graph
	 */
	public static boolean isVertexAdoptable(Vertex vertex, Graph<Vertex, Edge> graph) {
		return (isVertexAdoptable(vertex.getClass(), graph.getClass()));
	}

	/**
	 * check if edge is adoptable to graph
	 * 
	 * @param edgeClass
	 *            edge class to check
	 * @param graph
	 *            graph class to adopt the edge class
	 * @return return true if edgeClass is adoptable by graphClass
	 */
	public static boolean isEdgeAdoptable(Class<?> edgeClass, Class<?> graphClass) {
		try {
			Field field = graphClass.getDeclaredField("edgeWhiteList");
			Object object = field.get(null);
			if (!List.class.isInstance(object))
				return true;
			@SuppressWarnings("unchecked")
			List<Class<?>> edgeWhiteList = (List<Class<?>>) object;
			for (Class<?> clazz : edgeWhiteList) {
				if (clazz.isAssignableFrom(edgeClass))
					return true;
			}
			return false;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return true;
		}
	}

	/**
	 * check if vertex is adoptable to graph
	 * 
	 * @param vertexClass
	 *            vertex class to check
	 * @param graph
	 *            graph class to adopt the edge class
	 * @return return true if vertexClass is adoptable by graphClass
	 */
	public static boolean isVertexAdoptable(Class<?> vertexClass, Class<?> graphClass) {
		try {
			Field field = graphClass.getDeclaredField("vertexWhiteList");
			Object object = field.get(null);
			if (!List.class.isInstance(object))
				return true;
			@SuppressWarnings("unchecked")
			List<Class<?>> vertexWhiteList = (List<Class<?>>) object;
			for (Class<?> clazz : vertexWhiteList) {
				if (clazz.isAssignableFrom(vertexClass))
					return true;
			}
			return false;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return true;
		}
	}
}
