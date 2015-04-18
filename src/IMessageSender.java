

/**
 * Interface which a class must implement in order to send messages. A class that
 * implements this interface may send any type of message. The compatibility of the listener
 * to which this sender is attached should be computed in the addMessageListener method.
 * @author Christopher Crabtree
 *
 */
public interface IMessageSender {
	/**
	 * Add the listener to this sender's list of message listeners.
	 * @param listener The listener that will receive the messages emitted from this sender.
	 * @throws ClassCastException Thrown if the listener passed in is incompatible with this sender 
	 * i.e. the listener expects a different type of message than this sender emits.
	 */
	public void addMessageListener(Object listener) throws ClassCastException;
	/**
	 * Remove the listener from this sender's list of message listeners.
	 * @param listener The listener to be removed from the sender's list of message listeners.
	 */
	public void removeMessageListener(Object listener);
}
