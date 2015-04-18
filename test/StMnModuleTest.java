import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

import org.junit.Test;


public class StMnModuleTest {

	@Test
	public void testGetMessageSenders() {
		StMnModule module = new StMnModule();
		Object[] senders = module.getMessageSenders();
		for (Object sender : senders) {
			assertEquals(true, sender instanceof IMessageSender);
		}
	}

	@Test
	public void testGetMessageListeners() {
		StMnModule module = new StMnModule();
		Object[] listeners = module.getMessageListeners();
		for (Object listener : listeners) {
			assertEquals(true, listener instanceof IMessageListenerSensor);
		}
	}

	@Test
	public void testGetControlPanel() {
		StMnModule module = new StMnModule();
		JPanel controlPanel = (JPanel) module.getControlPanel();
		assertEquals(true, controlPanel.isVisible());
	}
	
	@Test
	public void testAddMessageListener() throws NoSuchFieldException, SecurityException, 
		IllegalArgumentException, IllegalAccessException {
		StMnModule module = new StMnModule();
		// Get the module's message listeners. Have to use reflection as the field is private.
		Field messageListenersField = StMnModule.class.getDeclaredField("midiListeners");
		messageListenersField.setAccessible(true);
		ArrayList<IMessageListenerMidi> messageListeners = (ArrayList<IMessageListenerMidi>) messageListenersField.get(module);
		IMessageListenerMidi listener = new IMessageListenerMidi() {
			public void receive(ShortMessage message) {
				// Don't need to do anything.
			}
		};
		module.addMessageListener(listener);
		// Check that the listener has been added to the module's message listeners array.
		assertEquals(listener, messageListeners.get(0));
	}
	
	@Test
	public void testRemoveMessageListener() throws NoSuchFieldException, SecurityException, 
	IllegalArgumentException, IllegalAccessException {
	StMnModule module = new StMnModule();
	// Get the module's message listeners. Have to use reflection as the field is private.
	Field messageListenersField = StMnModule.class.getDeclaredField("midiListeners");
	messageListenersField.setAccessible(true);
	ArrayList<IMessageListenerMidi> messageListeners = (ArrayList<IMessageListenerMidi>) messageListenersField.get(module);
	IMessageListenerMidi listener = new IMessageListenerMidi() {
		public void receive(ShortMessage message) {
			// Don't need to do anything.
		}
	};
	// Add message listener, then remove.
	module.addMessageListener(listener);
	module.removeMessageListener(listener);
	// Check that there is nothing in the module's message listener list.
	assertEquals(false, messageListeners.contains(listener));
	}
	
	@Test
	public void testReceive() throws NoSuchFieldException, SecurityException, 
		IllegalArgumentException, IllegalAccessException {
		StMnModule module = new StMnModule();
		final ArrayList<ShortMessage> receivedMessages = new ArrayList<ShortMessage>();
		// Create a dummy listener. This will add the message it receives to an arraylist. 
		// Then we can check the length of the array list after we've called module.receive()
		// to check that the listener has actually been sent the message.
		IMessageListenerMidi listener = new IMessageListenerMidi() {
			public void receive(ShortMessage message) {
				receivedMessages.add(message);
			}
		};
		// Add our listener to the module's listeners.
		module.addMessageListener(listener);
		// Send a sensor message to the module. Get a reference to its listener to do this.
		((IMessageListenerSensor) module.getMessageListeners()[0]).receive(new MessageSensor(900));
		// Allow a small amount of time for module to send message. We have to do 
		// this as the receive method starts a new thread for each listener. Checking
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
