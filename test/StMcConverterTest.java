import static org.junit.Assert.*;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JComboBox;
import javax.swing.JPanel;



import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.After;

import com.esotericsoftware.minlog.Log;

public class StMcConverterTest {

	@Test
	public void testGetControlPanel() {
		StMcConverter converter = new StMcConverter();
		JPanel controlPanel = converter.getControlPanel();
		assertEquals(true, controlPanel.isVisible());
	}

	@Test
	public void testGenerateMessage() {
		// Instantiate converter. Get GUI components so we can
		// set different values to test the conversion process.
		StMcConverter converter = new StMcConverter();
		JPanel controlPanel = converter.getControlPanel();
		JPanel row1 = (JPanel) controlPanel.getComponent(0);
		JPanel row2 = (JPanel) controlPanel.getComponent(1);
		JPanel row3 = (JPanel) controlPanel.getComponent(2);
		JComboBox modeSelector = (JComboBox) row1.getComponent(1);
		JComboBox channelSelector = (JComboBox) row2.getComponent(1);
		JComboBox controllerSelector = (JComboBox) row3.getComponent(1);
		ShortMessage result = null;
		MessageSensor sensorMessage = null;
		
		// Test for continuous convert mode.
		// Instantiate sensor message and set values for mode, channel 
		// and controller. 
		sensorMessage = new MessageSensor(400);
		modeSelector.setSelectedIndex(0);
		channelSelector.setSelectedIndex(5);
		controllerSelector.setSelectedIndex(47);
		
		try {
			result = converter.generateMessage(sensorMessage);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		
		assertEquals(ShortMessage.CONTROL_CHANGE, result.getCommand());
		assertEquals(5, result.getChannel());
		assertEquals(47, result.getData1());
		assertEquals(51, result.getData2());
		
		// Test for switch convert mode.
		sensorMessage = new MessageSensor(600);
		modeSelector.setSelectedIndex(1);
		channelSelector.setSelectedIndex(8);
		controllerSelector.setSelectedIndex(30);
		
		try {
			result = converter.generateMessage(sensorMessage);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		
		assertEquals(ShortMessage.CONTROL_CHANGE, result.getCommand());
		assertEquals(8, result.getChannel());
		assertEquals(30, result.getData1());
		assertEquals(127, result.getData2());
		
		// Test for continuous-to-switch convert mode.
		sensorMessage = new MessageSensor(200);
		modeSelector.setSelectedIndex(2);
		channelSelector.setSelectedIndex(12);
		controllerSelector.setSelectedIndex(99);
		
		try {
			result = converter.generateMessage(sensorMessage);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		
		assertEquals(ShortMessage.CONTROL_CHANGE, result.getCommand());
		assertEquals(12, result.getChannel());
		assertEquals(99, result.getData1());
		assertEquals(0, result.getData2());
	}

}
