


/**
 * Interface which a class must implement in order to receive messages of type MessageSensor.
 * @author Christopher Crabtree
 *
 */
public interface IMessageListenerSensor {
	/**
	 * Tell the class implementing IMessageListenerSensor to receive a message.
	 * @param message The message to be received.
	 */
	public void receive(MessageSensor message);
}
