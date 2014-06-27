package sadiasrc.graph;


public class IndexGraphElement implements IGraphElement{
	private IGraph<?, ?> owner;
	private int id;

	public IndexGraphElement(IGraph<?, ?> owner, int index) {
		this.owner = owner;
		this.id = index;
	}

	public boolean belongsTo(IGraph<?, ?> g) {
		return g == this.owner;
	}

	public int id() {
		return this.id;
	}

	public IGraph<?, ?> owner() {
		return this.owner;
	}

	/**
	 * @param id
	 *            the new id of this Object.
	 * @return true if the id changed from the call of this method, false
	 *         otherwise.
	 */
	public boolean setId(int id) {
		if (this.id == id) {
			return false;
		} else {
			this.id = id;
			return true;
		}
	}

	@Override
	public String toString() {
		return "[g:" + id() + "]";
	}
}
