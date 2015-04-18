
/**
 * DTO for sensor data.
 * @author Christopher Crabtree
 */
public class MessageSensor {
	private int value;
	
	/**
	 * Construct a MessageSensor with the value passed in.
	 * @param value The value to assign to this message.
	 */
	public MessageSensor(int value) {
		this.value = value;
	}
	
	/**
	 * Get the value of this message.
	 * @return The sensor value in the message.
	 */
	public int getValue() {
		return value;
	}
}
