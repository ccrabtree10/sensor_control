
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
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




public class StMnConverter implements KryoSerializable {
	private int rangePitchLow, rangePitchHigh, rangeVelocityLow, rangeVelocityHigh, pitch, velocity, channel;
	private float rangePitchRatio, rangeVelocityRatio;
	private transient JPanel controlPanel;
	private transient Method conversionMethod;
	private static final float CONVERSION_FACTOR = 0.127f;
	
	public StMnConverter() {
		rangePitchLow = 0;
		rangePitchHigh = 127;
		rangeVelocityLow = 0;
		rangeVelocityHigh = 127;
		pitch = 0;
		velocity = 0;
		channel = 0;
		init();
	}
	
	private void init() {
		// Create temp arrays for channel and controller values.
		Integer[] tempChannelArray = new Integer[16];
		for(int x=0; x<tempChannelArray.length; x++) {
			tempChannelArray[x] = x;
		}
		
		// Instantiate GUI components.
		final RangeSlider rangePitch = new RangeSlider(0, 127, 0, 127);
		final RangeSlider rangeVelocity = new RangeSlider(0, 127, 0, 127);
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		final JPanel rowChannel = new JPanel();
		final JPanel rowTrigger = new JPanel();
		final JPanel rowPitch = new JPanel();
		final JPanel rowVelocity = new JPanel();
		final JComboBox<Integer> channelSelector = new JComboBox<Integer>(new DefaultComboBoxModel<Integer>(tempChannelArray));
		
		// Size components.
		rangePitch.setPreferredSize(new Dimension(150, 30));
		rangeVelocity.setPreferredSize(new Dimension(150, 30));
		
		// Build GUI control panel.
		rowChannel.add(new JLabel("Channel"));
		rowChannel.add(channelSelector);
		rowTrigger.add(new JLabel("On/Off Values:"));
		rowPitch.add(new JLabel("Range: "));
		rowPitch.add(rangePitch);
		rowVelocity.add(new JLabel("Threshold: "));
		rowVelocity.add(rangeVelocity);
		
		controlPanel.add(rowChannel);
		controlPanel.add(rowTrigger);
		controlPanel.add(rowPitch);
		controlPanel.add(rowVelocity);
		
		// Add listeners for GUI components.
		channelSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				channel = channelSelector.getSelectedIndex();
				System.out.println(channel);
			}
		});
		
		rangePitch.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				rangePitchLow = rangePitch.getLowValue();
				rangePitchHigh = rangePitch.getHighValue();
				rangePitchRatio = (rangePitchHigh - rangePitchLow)/127;
			}
		});
		
		rangeVelocity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				rangeVelocityLow = rangeVelocity.getLowValue();
				rangeVelocityHigh = rangeVelocity.getHighValue();
				rangeVelocityRatio = (rangeVelocityHigh - rangeVelocityLow)/127;
			}
		});
		
		// Set GUI components selection.
		rangePitch.setLowValue(rangePitchLow);
		rangePitch.setHighValue(rangePitchHigh);
		rangeVelocity.setLowValue(rangePitchLow);
		rangeVelocity.setHighValue(rangePitchHigh);
		channelSelector.setSelectedItem(channel);
		
	}
	
	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, rangePitchLow);
		kryo.writeObject(output, rangePitchHigh);
		kryo.writeObject(output, rangeVelocityLow);
		kryo.writeObject(output, rangeVelocityHigh);
		kryo.writeObject(output, pitch);
		kryo.writeObject(output, velocity);
		kryo.writeObject(output, channel);
		kryo.writeObject(output, rangePitchRatio);
		kryo.writeObject(output, rangeVelocityRatio);
		
	}

	public void read(Kryo kryo, Input input) {
		rangePitchLow = kryo.readObject(input, Integer.class);
		rangePitchHigh = kryo.readObject(input, Integer.class);
		rangeVelocityLow = kryo.readObject(input, Integer.class);
		rangeVelocityHigh = kryo.readObject(input, Integer.class);
		pitch = kryo.readObject(input, Integer.class);
		velocity = kryo.readObject(input, Integer.class);
		channel = kryo.readObject(input, Integer.class);
		rangePitchRatio = kryo.readObject(input, Float.class);
		rangeVelocityRatio = kryo.readObject(input, Float.class);
		init();
	}
	
	public JPanel getControlPanel() {
		return controlPanel;
	}
	
	public ShortMessage generateMessage(MessageSensor message) throws InvalidMidiDataException {
		return new ShortMessage(ShortMessage.NOTE_ON, channel, pitch, velocity);
	}
	
	public void setPitch(MessageSensor message) {
		int midiValue = Math.round(message.getValue()*CONVERSION_FACTOR);
		pitch = Math.round((midiValue * rangePitchRatio) + rangePitchLow);
	}
	
	public void setVelocity(MessageSensor message) {
		int midiValue = Math.round(message.getValue()*CONVERSION_FACTOR);
		velocity = Math.round((midiValue * rangeVelocityRatio) + rangeVelocityLow);
	}

	
}	