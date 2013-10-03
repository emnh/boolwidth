package visualization;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JList;
import javax.swing.JScrollPane;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;

@SuppressWarnings("serial")
public class ListSelect<T> extends JScrollPane {

	final JList graphlist;
	// final JScrollPane graphlistscroller;
	final EventListModel<T> graphListModel;
	final SelectAction<T> selectaction;

	public ListSelect(EventList<T> graphEventList,
			final SelectAction<T> selectaction) {
		// create scrollable list
		super();
		this.graphListModel = new EventListModel<T>(graphEventList);
		this.graphlist = new JList(this.graphListModel);
		this.selectaction = selectaction;
		super.setViewportView(this.graphlist);
		// this.graphlistscroller = new JScrollPane(this.graphlist);

		// show graph on enter key press
		this.graphlist.addKeyListener(new KeyAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					selectaction.select((T) ListSelect.this.graphlist
							.getSelectedValue());
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void select(int idx) {
		this.graphlist.setSelectedIndex(idx);
		this.selectaction.select((T) this.graphlist.getSelectedValue());
	}
}
