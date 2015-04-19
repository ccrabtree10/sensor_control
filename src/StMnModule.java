

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.midi.*;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.text.DefaultEditorKit.InsertTabAction;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jidesoft.swing.RangeSlider;

/**
 * This module converts a sensor message to a MIDI note message. It has 3 inputs: a trigger input, a pitch 
 * input and a velocity input. The trigger input triggers a MIDI note message to be generated. The 
 * best result is usually achieved by connecting a switch sensor to this input. The pitch input controls 
 * sets the pitch of the MIDI note being generated and the velocity input controls the velocity (volume) 
 * of the MIDI note being generated.
 * @author Christopher Crabtree
 *
 */
public class StMnModule implements IModule, IMessageSender, KryoSerializable {
	private ArrayList<IMessageListenerMidi> midiListeners;
	private String[] listenerLabels;
	private ListenerTrigger listenerTrigger;
	private ListenerPitch listenerPitch;
	private ListenerVelocity listenerVelocity;
	private StMnConverter converter;
	private static int NOTE_ON_THRESHOLD = 500;

	public StMnModule() {	
		midiListeners = new ArrayList<IMessageListenerMidi>();
		
		listenerLabels = new String[3];
		listenerLabels[0] = "Trg";
		listenerLabels[1] = "Pit";
		listenerLabels[2] = "Vel";

		listenerTrigger = new ListenerTrigger();
		listenerPitch = new ListenerPitch();
		listenerVelocity = new ListenerVelocity();
		 
		converter = new StMnConverter();		
		init();
	}
	
	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, midiListeners);
		kryo.writeObject(output, listenerTrigger);
		kryo.writeObject(output, listenerPitch);
		kryo.writeObject(output, listenerVelocity);
		kryo.writeObject(output, listenerLabels);
		kryo.writeObject(output, converter);
	}

	public void read(Kryo kryo, Input input) {
		midiListeners = kryo.readObject(input, ArrayList.class);
		listenerTrigger = kryo.readObject(input, ListenerTrigger.class);
		listenerPitch = kryo.readObject(input, ListenerPitch.class);
		listenerVelocity = kryo.readObject(input, ListenerVelocity.class);
		listenerLabels = kryo.readObject(input, String[].class);
		converter = kryo.readObject(input, StMnConverter.class);
		init();
	}
	
	private void init() {
		listenerTrigger.setConverter(converter);
		listenerTrigger.setMidiListeners(midiListeners);
		listenerTrigger.init();
		listenerPitch.setConverter(converter);
		listenerVelocity.setConverter(converter);
		
		
		// Create checkbox for invert trigger control and add to control panel.
		final JCheckBox invertTriggerBox = new JCheckBox();
		invertTriggerBox.setSelected(listenerTrigger.invertTrigger);
		invertTriggerBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listenerTrigger.setInvertTrigger(invertTriggerBox.isSelected());
			}
		});
		JPanel row = new JPanel();
		row.add(new JLabel("Invert trigger: "));
		row.add(invertTriggerBox);
		converter.getControlPanel().add(row);
		
		
	}

	public Object[] getMessageSenders() {
		return new Object[]{this};
	}

	public Object[] getMessageListeners() {
		return new Object[]{listenerTrigger, listenerPitch, listenerVelocity};
	}

	public String toString() {
		return midiListeners.toString();
	}

	public JComponent getControlPanel() {		
		return converter.getControlPanel();
	}

	public void delete() {
		
	}

	public String getListenerLabel(int index) {
		return listenerLabels[index];
	}

	public String getSenderLabel(int index) {
		return "Out";
	}

	public String getLabel() {
		return "S-to-MIDI-Note";
	}

	/**
	 * Add the listener to the module's list of listeners.
	 * @param listener The listener to add.
	 */
	public void addMessageListener(Object listener) throws ClassCastException {
		IMessageListenerMidi midiListener = (IMessageListenerMidi) listener;
		midiListeners.add(midiListener);	
	}

	/**
	 * Remove the listener from this module's list of listeners.
	 * @param listener The listener to remove.
	 */
	public void removeMessageListener(Object listener) {
		midiListeners.remove(listener);		
	}

	private class ListenerTrigger implements IMessageListenerSensor {
		private transient StMnConverter thisConverter;
		private transient ArrayList<IMessageListenerMidi> thisMidiListeners;
		private transient ExecutorService exe;
		private boolean invertTrigger;
		public synchronized void receive(MessageSensor message) {
			// Only send a note on message if the sensor is "on", that is, above the note on threshold.
			// If invertTrigger is true, then the inverse is true.
			if ((!invertTrigger && message.getValue() > NOTE_ON_THRESHOLD) || (invertTrigger && message.getValue() < NOTE_ON_THRESHOLD)) {
				try {
					final ShortMessage midiMessage = thisConverter.generateMessage(message);
					Iterator<IMessageListenerMidi> iterator = thisMidiListeners.iterator();
					while(iterator.hasNext()) {
						final IMessageListenerMidi midiListener = (IMessageListenerMidi) iterator.next();
						exe.execute(new Runnable() {
							public void run() {
								midiListener.receive(midiMessage);
							}
						});
					}
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}
		}
		
		protected void init() {
			exe = Executors.newCachedThreadPool();
		}
		
		protected void setConverter(StMnConverter converter) {
			this.thisConverter = converter;
		}
		
		protected void setMidiListeners(ArrayList<IMessageListenerMidi> listeners) {
			this.thisMidiListeners = listeners;
		}
		
		protected void setInvertTrigger(boolean invertTrigger) {
			this.invertTrigger = invertTrigger;
		}
	}
	
	private class ListenerPitch implements IMessageListenerSensor {
		private transient StMnConverter thisConverter;
		public void receive(MessageSensor message) {
			thisConverter.setPitch(message);
		}
		
		protected void setConverter(StMnConverter converter) {
			this.thisConverter = converter;
		}
	}
	
	private class ListenerVelocity implements IMessageListenerSensor {
		private transient StMnConverter thisConverter;
		public void receive(MessageSensor message) {
			thisConverter.setVelocity(message);
		}
		
		protected void setConverter(StMnConverter converter) {
			this.thisConverter = converter;
		}
	}
	
	
}
