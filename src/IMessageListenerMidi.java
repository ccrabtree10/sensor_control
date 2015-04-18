
import java.io.Serializable;

import javax.sound.midi.ShortMessage;

/**
 * Interface which a class must implement in order to receive messages of type ShortMessage.
 * @author Christopher Crabtree
 *
 */
public interface IMessageListenerMidi {
	/**
	 * Tell the class implementing IMessageListenerMidi to receive a message.
	 * @param message The message to be received.
	 */
	public void receive(ShortMessage message);
}
