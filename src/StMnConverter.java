
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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




public class StMnConverter {
	private transient RangeSlider rangePitch, rangeVelocity;
	private transient JLabel pitchRangeLabel,velocityRangeLabel, triggerLabel;
	private transient int rangePitchMin, rangePitchMax, rangeVelocityMin, rangeVelocityMax, pitch, velocity;
	private transient float rangePitchRatio, rangeVelocityRatio;
	private transient JPanel controlPanel, rowTrigger, rowPitch, rowVelocity;
	private transient Method conversionMethod;
	private static final float CONVERSION_FACTOR = 0.127f;
	
	public StMnConverter() {
		Object[] tempChannelArray = new Object[16];
		Object[] tempControllerArray = new Object[128];
		for(int x=0; x<tempChannelArray.length; x++) {
			tempChannelArray[x] = x;
		}
		for(int x=0; x<tempControllerArray.length; x++) {
			tempControllerArray[x] = x;
		}
		
		pitchRangeLabel = new JLabel("Range: ");
		velocityRangeLabel = new JLabel("Threshold: ");
		triggerLabel = new JLabel("On/Off Values:");
		
		rangePitch = new RangeSlider(0, 127, 0, 127);
		rangeVelocity = new RangeSlider(0, 127, 0, 127);		
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		
		rowTrigger = new JPanel();
		rowPitch = new JPanel();
		rowVelocity = new JPanel();
		
		
		rowTrigger.add(triggerLabel);
		rowPitch.add(pitchRangeLabel);
		rowPitch.add(rangePitch);
		rowVelocity.add(velocityRangeLabel);
		rowVelocity.add(rangeVelocity);
		
				
		controlPanel.add(rowTrigger);
		controlPanel.add(rowPitch);
		controlPanel.add(rowVelocity);
		
		rangePitch.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				rangePitchRatio = (rangePitch.getHighValue() - rangePitch.getLowValue())/127;
			}
		});
		
		rangeVelocity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				rangeVelocityRatio = (rangeVelocity.getHighValue() - rangeVelocity.getLowValue())/127;
			}
		});
		
		rangePitchRatio = (rangePitch.getHighValue() - rangePitch.getLowValue())/127;
		rangeVelocityRatio = (rangeVelocity.getHighValue() - rangeVelocity.getLowValue())/127;		
	}
	
	public JPanel getControlPanel() {
		return controlPanel;
	}
	
	public ShortMessage generateMessage(MessageSensor message) throws InvalidMidiDataException {
		return new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, velocity);
	}
	
	public void setPitch(MessageSensor message) {
		int midiValue = Math.round(message.getValue()*CONVERSION_FACTOR);
		pitch = Math.round((midiValue * rangePitchRatio) + rangePitch.getLowValue());
		System.out.println(pitch);
	}
	
	public void setVelocity(MessageSensor message) {
		int midiValue = Math.round(message.getValue()*CONVERSION_FACTOR);
		velocity = Math.round((midiValue * rangeVelocityRatio) + rangeVelocity.getLowValue());
		System.out.println(velocity);
	}
}	