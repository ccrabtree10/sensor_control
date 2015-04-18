import static org.junit.Assert.*;

import java.lang.reflect.Field;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.junit.Test;


public class StMnConverterTest {

	@Test
	public void testGetControlPanel() {
		StMnConverter converter = new StMnConverter();
		JPanel controlPanel = converter.getControlPanel();
		assertEquals(true, controlPanel.isVisible());
	}

	@Test
	public void testGenerateMessage() {
		// Instantiate converter. Get GUI components so we can
		// set different values to test the conversion process.
		StMnConverter converter = new StMnConverter();
		JPanel controlPanel = converter.getControlPanel();
		JPanel row1 = (JPanel) controlPanel.getComponent(0);
		JComboBox channelSelector = (JComboBox) row1.getComponent(1);
		ShortMessage result = null;
		
		// Instantiate sensor messages for trigger, pitch and velocity 
		// and set channel value.
		MessageSensor triggerMessage = new MessageSensor(400);
		MessageSensor pitchMessage = new MessageSensor(600);
		MessageSensor velocityMessage = new MessageSensor(850);
		
		channelSelector.setSelectedIndex(5);
		converter.setPitch(pitchMessage);
		converter.setVelocity(velocityMessage);
		
		try {
			result = converter.generateMessage(triggerMessage);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		
		assertEquals(ShortMessage.NOTE_ON, result.getCommand());
		assertEquals(5, result.getChannel());
		assertEquals(76, result.getData1());
		assertEquals(108, result.getData2());
	}

	@Test
	public void testSetPitch() throws NoSuchFieldException, SecurityException, 
		IllegalArgumentException, IllegalAccessException {
		StMnConverter converter = new StMnConverter();
		Field pitchField = StMnConverter.class.getDeclaredField("pitch");
		pitchField.setAccessible(true);
		converter.setPitch(new MessageSensor(250));
		assertEquals(32, pitchField.get(converter));
	}

	@Test
	public void testSetVelocity() throws NoSuchFieldException, SecurityException, 
		IllegalArgumentException, IllegalAccessException {
		StMnConverter converter = new StMnConverter();
		Field velocityField = StMnConverter.class.getDeclaredField("velocity");
		velocityField.setAccessible(true);
		converter.setVelocity(new MessageSensor(500));
		assertEquals(64, velocityField.get(converter));
	}
}
