
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jidesoft.swing.RangeSlider;
import com.phidgets.event.SensorChangeEvent;



/**
 * This class is used to by the StMcModule to convert a sensor message to a MIDI message.
 * @author Christopher Crabtree
 *
 */
public class StMcConverter implements KryoSerializable {
	private int channel, controller, rangeMin, rangeMax, onOffSwitchMin, 
		onOffSwitchMax, conToSwitchThreshold;
	private String conMethodName;
	private transient JPanel controlPanel;
	private transient Method conversionMethod;
	private static final float CONVERSION_FACTOR = 0.127f;
	
	public StMcConverter() {
		// Set initial values.
		channel = 0;
		controller = 0;
		rangeMin = 0;
		rangeMax = 127;
		onOffSwitchMin = 0;
		onOffSwitchMax = 127;
		conToSwitchThreshold = 63;
		conMethodName = "convertContinuous";
		// Initialise GUI components etc.
		init();
	}

	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, channel);
		kryo.writeObject(output, controller);
		kryo.writeObject(output, rangeMin);
		kryo.writeObject(output, rangeMax);
		kryo.writeObject(output, onOffSwitchMin);
		kryo.writeObject(output, onOffSwitchMax);
		kryo.writeObject(output, conToSwitchThreshold);
		kryo.writeObject(output, conMethodName);
	}

	public void read(Kryo kryo, Input input) {
		channel = kryo.readObject(input, int.class);
		controller = kryo.readObject(input, int.class);
		rangeMin = kryo.readObject(input, int.class);
		rangeMax = kryo.readObject(input, int.class);
		onOffSwitchMin = kryo.readObject(input, int.class);
		onOffSwitchMax =  kryo.readObject(input, int.class);
		conToSwitchThreshold = kryo.readObject(input, int.class);
		conMethodName = kryo.readObject(input, String.class);
		init();
	}
	
	private void init() {
		// Make temp arrays to hold integer values.
		Integer[] tempChannelArray = new Integer[16];
		Integer[] tempControllerArray = new Integer[128];
		for(int x=0; x<tempChannelArray.length; x++) {
			tempChannelArray[x] = x;
		}
		for(int x=0; x<tempControllerArray.length; x++) {
			tempControllerArray[x] = x;
		}
		
		// Instantiate GUI components.
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		final JPanel rowMode = new JPanel();
		final JPanel rowChannel = new JPanel();
		final JPanel rowController = new JPanel();
		final JPanel rowRange = new JPanel();
		final JPanel rowThreshold = new JPanel();
		final JPanel rowOnOffSwitch = new JPanel();
		
		final JComboBox<Integer> channelSelector = new JComboBox<Integer>(new DefaultComboBoxModel<Integer>(tempChannelArray));
		final JComboBox<Integer> controllerSelector = new JComboBox<Integer>(new DefaultComboBoxModel<Integer>(tempControllerArray));
		final RangeSlider rangeControl = new RangeSlider(0, 127, 0, 127);
		final RangeSlider onOffSwitchControl = new RangeSlider(0, 127, 0, 127);
		final JSlider thresholdControl = new JSlider();
		
		final JComboBox<String> modeSelector = new JComboBox<String>(new DefaultComboBoxModel<String>());
		modeSelector.addItem("Continuous");
		modeSelector.addItem("Switch");
		modeSelector.addItem("Con > Swi");
		
		// Size components.
		rangeControl.setPreferredSize(new Dimension(150, 30));
		onOffSwitchControl.setPreferredSize(new Dimension(150, 30));
		thresholdControl.setPreferredSize(new Dimension(150, 30));
		
		// Build control panel.
		rowMode.add(new JLabel("Mode: "));
		rowMode.add(modeSelector);
		rowChannel.add(new JLabel("Channel "));
		rowChannel.add(channelSelector);
		rowController.add(new JLabel("Controller: "));
		rowController.add(controllerSelector);
		rowRange.add(new JLabel("Range: "));
		rowRange.add(rangeControl);
		rowThreshold.add(new JLabel("Threshold: "));
		rowThreshold.add(thresholdControl);
		rowOnOffSwitch.add(new JLabel("On/Off Values:"));
		rowOnOffSwitch.add(onOffSwitchControl);
				
		controlPanel.add(rowMode);
		controlPanel.add(rowChannel);
		controlPanel.add(rowController);
		controlPanel.add(rowRange);
		
		channelSelector.setSelectedItem(channel);
		controllerSelector.setSelectedItem(controller);
		rangeControl.setLowValue(rangeMin);
		rangeControl.setHighValue(rangeMax);
		
		onOffSwitchControl.setLowValue(onOffSwitchMin);
		onOffSwitchControl.setHighValue(onOffSwitchMax);
		thresholdControl.setValue(conToSwitchThreshold);
		
		try {
			conversionMethod = StMcConverter.class.getDeclaredMethod(conMethodName, MessageSensor.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		modeSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// Update view and set conversion method.
				controlPanel.remove(rowRange);
				controlPanel.remove(rowThreshold);
				controlPanel.remove(rowOnOffSwitch);
				try {
					switch (modeSelector.getSelectedIndex()) {
						case 0: controlPanel.add(rowRange);
								conversionMethod = StMcConverter.class.getDeclaredMethod("convertContinuous", MessageSensor.class);
								break;
						case 1: controlPanel.add(rowOnOffSwitch);
								conversionMethod = StMcConverter.class.getDeclaredMethod("convertSwitch", MessageSensor.class);
								break;
						case 2: controlPanel.add(rowThreshold);
								conversionMethod = StMcConverter.class.getDeclaredMethod("convertConToSwitch", MessageSensor.class);
								break;
					}
				} 
				catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
				controlPanel.revalidate();
			}
		});
		
		channelSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				channel = (int) channelSelector.getSelectedItem();
				System.out.println("listener channel");
			}
		});
		
		controllerSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				controller = (int) controllerSelector.getSelectedItem();
			}
		});

		rangeControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				rangeMin = rangeControl.getLowValue();
				rangeMax = rangeControl.getHighValue();
			}
		});
		
		onOffSwitchControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				onOffSwitchMin = onOffSwitchControl.getLowValue();
				onOffSwitchMax = onOffSwitchControl.getHighValue();
			}
		});
		
		thresholdControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				conToSwitchThreshold = thresholdControl.getValue();
			}
		});
		
		try {
			switch (modeSelector.getSelectedIndex()) {
				case 0: controlPanel.add(rowRange);
						conversionMethod = StMcConverter.class.getDeclaredMethod("convertContinuous", MessageSensor.class);
						break;
				case 1: controlPanel.add(rowOnOffSwitch);
						conversionMethod = StMcConverter.class.getDeclaredMethod("convertSwitch", MessageSensor.class);
						break;
				case 2: controlPanel.add(rowThreshold);
						conversionMethod = StMcConverter.class.getDeclaredMethod("convertConToSwitch", MessageSensor.class);
						break;
			}
		} 
		catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}		
	}
	
	public JPanel getControlPanel() {
		return controlPanel;
	}
	
	/**
	 * Convert the sensor message to a MIDI message, using the currently selected conversion 
	 * process.
	 * @param message The sensor message to convert.
	 * @return The converted MIDI message.
	 * @throws InvalidMidiDataException
	 */
	public ShortMessage generateMessage(MessageSensor message) throws InvalidMidiDataException {
		ShortMessage midiMessage = null;
		try {
			midiMessage = (ShortMessage) conversionMethod.invoke(this, message);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return midiMessage;
	}
	
	// This converts a message generated by a continuous sensor such as a precision light sensor, 
	// joystick, slider etc. The value of the message is scaled according to the values rangeMin
	// and rangeMax. These are adjustable via a slider in the module control panel.
	private ShortMessage convertContinuous(MessageSensor message) throws InvalidMidiDataException {
		int midiValue = Math.round(message.getValue()*CONVERSION_FACTOR);
		float ratio = (float) (rangeMax - rangeMin)/127;
		midiValue = Math.round((midiValue * ratio) + rangeMin);
		return new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controller, midiValue);
	}
	
	// This converts a message generated by a switch sensor such as a proximity sensor or a touch
	// sensor. It outputs a midi message with one of two values. These values are determined by
	// onOffSwitchMin (the value output when the switch is "off") and onOffSwitchMax (the value
	// output when the switch is "on"). These variables are adjustable via a slider in the module
	// control panel.
	private ShortMessage convertSwitch(MessageSensor message) throws InvalidMidiDataException {
		int midiValue;
		if (message.getValue() < 100) {
			midiValue = onOffSwitchMin;
		} else {
			midiValue = onOffSwitchMax;
		}
		return new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controller, midiValue);
	}
	
	// This converts a contnuous sensor message to a midi message that has one of two values. The two
	// values are 0 ("on") or 127 ("off"). If the sensor message received is below the conToSwitchThreshold,
	// then the midi message generated will have a value of 0. If the sensor message received has a
	// value higher than the conToSwitchThreshold, then the midi message will have a value of 127.
	private ShortMessage convertConToSwitch(MessageSensor message) throws InvalidMidiDataException {
		int midiValue;
		// Multiply threshold by 10 because the default JSlider's BoundedRangeModel has a range of
		// 0 - 100, whereas a MessageSensor can have a range of 0 - 1000.
		if(message.getValue() < conToSwitchThreshold*10) {
			midiValue = 0;
		} else {
			midiValue = 127;
		}
		return new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controller, midiValue);
	}
	
	// Note: these methods are not used in the application, they were used in testing to compare 
	// the speed of Java native serialization and Kryo serilaization.
	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeObject(channel);
		output.writeObject(controller);
		output.writeObject(rangeMin);
		output.writeObject(rangeMax);
		output.writeObject(onOffSwitchMin);
		output.writeObject(onOffSwitchMax);
		output.writeObject(conToSwitchThreshold);
		output.writeObject(conMethodName);
	}
	
	private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
		channel = (int) input.readObject();
		controller = (int) input.readObject();
		rangeMin = (int) input.readObject();
		rangeMax = (int) input.readObject();
		onOffSwitchMin = (int) input.readObject();
		onOffSwitchMax =  (int) input.readObject();
		conToSwitchThreshold = (int) input.readObject();
		conMethodName = (String) input.readObject();
		init();
	}	
}
