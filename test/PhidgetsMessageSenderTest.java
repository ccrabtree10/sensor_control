import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.sound.midi.ShortMessage;

import org.junit.Test;

import com.phidgets.event.SensorChangeEvent;


public class PhidgetsMessageSenderTest {

	@Test
	public void testAddMessageListener() throws NoSuchFieldException, SecurityException, 
		IllegalArgumentException, IllegalAccessException {
		PhidgetsMessageSender sender = new PhidgetsMessageSender(0);
		// Get the sender's message listeners. Have to use reflection as the field is private.
		Field messageListenersField = PhidgetsMessageSender.class.getDeclaredField("listeners");
		messageListenersField.setAccessible(true);
		ArrayList<IMessageListenerMidi> messageListeners = (ArrayList<IMessageListenerMidi>) messageListenersField.get(sender);
		IMessageListenerSensor listener = new IMessageListenerSensor() {
			public void receive(MessageSensor message) {
				// Don't need to do anything.
			}
		};
		sender.addMessageListener(listener);
		// Check that the listener has been added to the sender's message listeners array.
		assertEquals(listener, messageListeners.get(0));
	}
	
	@Test
	public void testRemoveMessageListener() throws NoSuchFieldException, SecurityException, 
	IllegalArgumentException, IllegalAccessException {
		PhidgetsMessageSender sender = new PhidgetsMessageSender(0);
	// Get the sender's message listeners. Have to use reflection as the field is private.
	Field messageListenersField = PhidgetsMessageSender.class.getDeclaredField("listeners");
	messageListenersField.setAccessible(true);
	ArrayList<IMessageListenerMidi> messageListeners = (ArrayList<IMessageListenerMidi>) messageListenersField.get(sender);
	IMessageListenerSensor listener = new IMessageListenerSensor() {
		public void receive(MessageSensor message) {
			// Don't need to do anything.
		}
	};
	// Add message listener, then remove.
	sender.addMessageListener(listener);
	sender.removeMessageListener(listener);
	// Check that there is nothing in the sender's message listener list.
	assertEquals(false, messageListeners.contains(listener));
	}
	
	@Test
	public void testReceive() throws NoSuchFieldException, SecurityException, 
		IllegalArgumentException, IllegalAccessException {
		PhidgetsMessageSender sender = new PhidgetsMessageSender(0);
		final ArrayList<MessageSensor> receivedMessages = new ArrayList<MessageSensor>();
		// Create a dummy listener. This will add the message it receives to an arraylist. 
		// Then we can check the length of the array list after we've called sender.send()
		// to check that the listener has actually been sent the message.
		IMessageListenerSensor listener = new IMessageListenerSensor() {
			public void receive(MessageSensor message) {
				receivedMessages.add(message);
			}
		};
		// Add our listener to the module's listeners.
		sender.addMessageListener(listener);
		// Send a sensor message to the module.
		sender.send(new SensorChangeEvent(null, 0, 0));
		// Allow a small amount of time for sender to send message. We have to do 
		// this as the send method starts a new thread for each listener. Checking
		// the size of the receivedMessages arraylist immediately will probably return
		// 0 as the module has not had chance to sent its message yet.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		// Finally, check that the size of receivedMessages is 1. That is, the listener
		// has received the message and put it in the array list.
		assertEquals(true, receivedMessages.size() == 1);
	}

}
