package visualization;

import graph.Vertex;
import interfaces.IGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

// TODO: file is deprecated, slated for removal

@SuppressWarnings("serial")
public class GraphBrowser extends JFrame {

	public <TVertex extends Vertex<V>, V, E> GraphBrowser(
			Collection<? extends IGraph<TVertex, V, E>> graphs) {
		super("Graph browser");
		final ShowGraph graphview = new ShowGraph();
		final JPanel panel = new JPanel();

		// setLayout(new GridLayout(1, 1));
		// setContentPane(panel);
		add(panel);

		GridBagLayout gridbag = new GridBagLayout();
		panel.setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();

		// fill list
		EventList<IGraph<TVertex, V, E>> graphEventList = new BasicEventList<IGraph<TVertex, V, E>>();
		graphEventList.addAll(graphs);

		// create list UI component
		ListSelect<IGraph<TVertex, V, E>> ls = new ListSelect<IGraph<TVertex, V, E>>(
				graphEventList, new SelectAction<IGraph<TVertex, V, E>>() {
					@Override
					public void select(IGraph<TVertex, V, E> graph) {
						graphview.setGraph(graph);
					}
				});

		// add components
		panel.add(ls);
		panel.add(graphview);

		// layout
		c.fill = GridBagConstraints.VERTICAL;
		gridbag.setConstraints(ls, c);

		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(graphview, c);

		// handle resize
		// addComponentListener(new ComponentAdapter() {
		// @Override
		// public void componentResized(ComponentEvent e) {
		// panel.setSize(GraphBrowser.this.getSize());
		// }
		// });

		panel.setSize(800, 600);
		pack();
	}
}