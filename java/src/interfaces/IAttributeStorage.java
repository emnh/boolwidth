package interfaces;

public interface IAttributeStorage {
	public <T> T getAttr(String key);

	public boolean hasAttr(String key);

	public void setAttr(String key, Object value);
}
