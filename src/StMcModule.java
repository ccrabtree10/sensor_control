

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jidesoft.swing.RangeSlider;
/**
 * This module converts sensor messages to MIDI messages. It has one input which accepts 
 * messages of type MessageSensor and one output which sends the converted MIDI messages 
 * of type ShortMessage.
 * @author Christopher Crabtree
 */
public class StMcModule implements IModule, IMessageListenerSensor, IMessageSender, KryoSerializable {
	private ArrayList<IMessageListenerMidi> midiListeners;
	private StMcConverter converter;
	private transient ExecutorService exe;
	
	
	public StMcModule() {
		midiListeners = new ArrayList<IMessageListenerMidi>();
		converter = new StMcConverter();
		exe = Executors.newCachedThreadPool();
	}

	public Object[] getMessageSenders() {
		return new Object[]{this};
	}

	public Object[] getMessageListeners() {
		return new Object[]{this};
	}

	public JComponent getControlPanel() {		
		return converter.getControlPanel();
	}

	public void delete() {
		
	}

	public String getListenerLabel(int index) {
		return "In";
	}

	public String getSenderLabel(int index) {
		return "Out";
	}

	public String getLabel() {
		return "S-to-MIDI-Con";
	}

	/**
	 * Convert this sensor message to a MIDI message and send to any MIDI listeners connected to 
	 * this module.
	 * @param message The sensor message to convert and send.
	 */
	public void receive(MessageSensor message) {
		try {
			final ShortMessage midiMessage = converter.generateMessage(message);
			Iterator<IMessageListenerMidi> iterator = midiListeners.iterator();
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

	/**
	 * Add the listener to this module's list of listeners.
	 * @param listener The listener to add.
	 * @throws ClassCastException Thrown if the listener passed in is not a MIDI listener i.e. does 
	 * not implement IMessageListenerMidi.
	 */
	public void addMessageListener(Object listener) throws ClassCastException {
		IMessageListenerMidi midiListener = (IMessageListenerMidi) listener;
		midiListeners.add(midiListener);
	}

	/**
	 * Remove this listener from the module's list of midi listeners.
	 * @param listener The listener to remove.
	 */
	public void removeMessageListener(Object listener) {
		midiListeners.remove(listener);
	}

	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, midiListeners);
		kryo.writeObject(output, converter);
	}

	public void read(Kryo kryo, Input input) {
		midiListeners = kryo.readObject(input, ArrayList.class);
		converter = kryo.readObject(input, StMcConverter.class);
		exe = Executors.newCachedThreadPool();
	}
	
	// Note: these methods are not used in the application, they were used in testing to compare 
	// the speed of Java native serialization and Kryo serilaization.
	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeObject(midiListeners);
		output.writeObject(converter);
	}
	
	private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
		midiListeners = (ArrayList<IMessageListenerMidi>) input.readObject();
		converter = (StMcConverter) input.readObject();
	}
}
