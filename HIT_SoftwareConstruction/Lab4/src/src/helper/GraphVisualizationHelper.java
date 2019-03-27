package helper;

import java.awt.Dimension;

import javax.swing.JFrame;

import edge.Edge;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import org.apache.commons.collections15.Transformer;

import graph.Graph;
import util.GraphConverter;
import vertex.Vertex;

public class GraphVisualizationHelper {

	public static final int DEFAULT_WIDTH = 1024;
	public static final int DEFAULT_HEIGHT = 768;

	/**
	 * visualize a Graph
	 * 
	 * @param g
	 *            Graph to visualize
	 */
	public static void visualize(Graph<Vertex, Edge> g) {
		JFrame window = new JFrame("");

		edu.uci.ics.jung.graph.Graph<Vertex, Edge> graph = GraphConverter.convert2Jung(g);

		// set layout
		Layout<Vertex, Edge> layout = new CircleLayout<>(graph);
		layout.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

		// set viewer
		VisualizationViewer<Vertex, Edge> viewer = new VisualizationViewer<>(layout);
		viewer.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		viewer.getRenderContext().setVertexLabelTransformer(new VertexTransformer());
		viewer.getRenderContext().setEdgeLabelTransformer(new EdgeTransformer());
		viewer.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		// and
		DefaultModalGraphMouse<Vertex, Edge> modalGraphMouse = new DefaultModalGraphMouse<>();
		modalGraphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		viewer.setGraphMouse(modalGraphMouse);

		// set JFrame
		window.setTitle(g.getName());
		// window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().add(viewer);
		window.pack();
		window.setVisible(true);
	}

	/**
	 * class to transform vertex to its label
	 */
	protected static class VertexTransformer implements Transformer<Vertex, String> {
		@Override
		public String transform(Vertex input) {
			return input.toString();
		}
	}

	/**
	 * class to transform edge to its label and weight. if the edge is not directed,
	 * its weight will not be shown
	 */
	protected static class EdgeTransformer implements Transformer<Edge, String> {

		@Override
		public String transform(Edge input) {
			if (input.getWeight() < 0)
				return input.toString();
			else
				return input.toString() + ":" + input.getWeight();
		}

	}
}
