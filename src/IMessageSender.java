


public interface IMessageSender {
	public void addMessageListener(Object listener) throws ClassCastException;
	public void removeMessageListener(Object listener);
	public String toString();
}
